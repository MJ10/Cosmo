package io.mokshjn.cosmo.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.media.MediaMetadataCompat;

import java.util.ArrayList;
import java.util.Iterator;

import io.mokshjn.cosmo.utils.LibUtils;

/**
 * Created by moksh on 19/3/17.
 */

public class LibrarySource implements MusicProviderSource {

    private ContentResolver resolver;
    final public static Uri sArtworkUri = Uri
            .parse("content://media/external/audio/albumart");

    public LibrarySource(ContentResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public Iterator<MediaMetadataCompat> iterator() {
        ArrayList<MediaMetadataCompat> tracks = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String sortOrder = MediaStore.Audio.AudioColumns.TITLE + " COLLATE LOCALIZED ASC";
        Cursor cursor = resolver.query(uri, null, null, null, sortOrder);
        if(cursor != null && cursor.moveToFirst()) {
            do {
                MediaMetadataCompat metadata = new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, String.valueOf(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns._ID))))
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM)))
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST)))
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)))
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION)))
                        .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TRACK)))
                        .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION)))
                        .putString(MediaMetadataCompat.METADATA_KEY_GENRE, "Songs")
                        .putString(MusicProviderSource.CUSTOM_METADATA_TRACK_SOURCE, LibUtils.getSongFileUri(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns._ID))).toString())
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, ContentUris.withAppendedId(sArtworkUri, cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID))).toString())
                        .build();
                tracks.add(metadata);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return tracks.iterator();
    }
}
