package io.mokshjn.cosmo.utils;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by moksh on 15/3/17.
 */

public class LibUtils {
    public static final String TAG = LibUtils.class.getSimpleName();

    public static Uri getMediaStoreAlbumCoverUri(int albumId) {
        final Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");

        return ContentUris.withAppendedId(sArtworkUri, albumId);
    }

    public static Uri getSongFileUri(long songId) {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId);
    }

    public static String getReadableDurationString(long songDurationMillis) {
        long minutes = (songDurationMillis / 1000) / 60;
        long seconds = (songDurationMillis / 1000) % 60;
        return String.format("%01d:%02d", minutes, seconds);
    }

    public static boolean isArtistNameUnknown(@Nullable String artistName) {
        if (TextUtils.isEmpty(artistName)) return false;
        artistName = artistName.trim().toLowerCase();
        return artistName.equals("unknown") || artistName.equals("<unknown>");
    }


}
