package io.mokshjn.cosmo.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import io.mokshjn.cosmo.helpers.MediaIDHelper;

/**
 * Created by moksh on 15/3/17.
 */

public class LibUtils {
    public static final String TAG = LibUtils.class.getSimpleName();

    public static Uri getMediaStoreAlbumCoverUri(long albumId) {
        final Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");

        return ContentUris.withAppendedId(sArtworkUri, albumId);
    }

    public static Uri getSongFileUri(long songId) {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId);
    }

    public static String getAlbumByAlbumId(long albumId, ContentResolver resolver) {
        String album = "";
        String selection = "_id = " + albumId;
        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor cursor = resolver.query(uri, null, selection, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
            cursor.close();
        }
        return album;
    }

    public static SongInfo getTrackInfo(String mediaId, ContentResolver resolver) {
        SongInfo info = null;
        long id = Long.parseLong(MediaIDHelper.extractMusicIDFromMediaID(mediaId));
        String selection = "_id = " + String.valueOf(id);
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = resolver.query(uri, null, selection, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
            long trackNumber = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TRACK));

            info = new SongInfo(duration, trackNumber);
        }
        cursor.close();

        return info;
    }


    public static String getReadableDurationString(long songDurationMillis) {
        long minutes = (songDurationMillis / 1000) / 60;
        long seconds = (songDurationMillis / 1000) % 60;
        return String.format("%01d:%02d", minutes, seconds);
    }

    public static String getTrackNumber(long track) {
        if (track == 0) {
            return "-";
        } else if (track > 2000) {
            return String.valueOf(track % 2000);
        } else {
            return String.valueOf(track % 1000);
        }
    }

    public static boolean isArtistNameUnknown(@Nullable String artistName) {
        if (TextUtils.isEmpty(artistName)) return false;
        artistName = artistName.trim().toLowerCase();
        return artistName.equals("unknown") || artistName.equals("<unknown>");
    }

    public static class SongInfo {
        private long duration, trackNumber;

        SongInfo(long duration, long trackNumber) {
            this.duration = duration;
            this.trackNumber = trackNumber;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public long getTrackNumber() {
            return trackNumber;
        }

        public void setTrackNumber(long trackNumber) {
            this.trackNumber = trackNumber;
        }


    }
}
