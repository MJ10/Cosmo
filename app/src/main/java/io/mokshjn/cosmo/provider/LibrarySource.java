package io.mokshjn.cosmo.provider;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

import io.mokshjn.cosmo.utils.LibUtils;

/**
 * Created by moksh on 19/3/17.
 */

public class LibrarySource implements MusicProviderSource {

    private ContentResolver resolver;

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
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID)))
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST)))
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)))
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION)))
                        .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TRACK)))
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, LibUtils.getMediaStoreAlbumCoverUri(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))).toString())
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM)))
                        .build();
                tracks.add(metadata);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return tracks.iterator();
    }

    @Override
    public Iterator<MediaMetadataCompat> albums() {
        ArrayList<MediaMetadataCompat> album = new ArrayList<>();
        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String sortOrder = MediaStore.Audio.AlbumColumns.ALBUM + " COLLATE LOCALIZED ASC";
        Cursor cursor = resolver.query(uri, null, null, null, sortOrder);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                MediaMetadataCompat mediaMetadataCompat = new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)))
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)))
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, String.valueOf(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Albums._ID))))
                        .build();
                album.add(mediaMetadataCompat);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return album.iterator();
    }

    @Override
    public Iterator<MediaMetadataCompat> artists() {
        ArrayList<MediaMetadataCompat> artists = new ArrayList<>();
        Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        String sortOrder = MediaStore.Audio.ArtistColumns.ARTIST + " COLLATE LOCALIZED ASC";
        Cursor cursor = resolver.query(uri, null, null, null, sortOrder);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long tracks = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.NUMBER_OF_TRACKS));
                long albs = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.NUMBER_OF_ALBUMS));
                String subtitle = albs + " Albums - " + tracks + " Songs";
                Log.d("TAG", "artists: " + String.valueOf(tracks));
                MediaMetadataCompat mediaMetadataCompat = new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.ARTIST)))
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, subtitle)
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, String.valueOf(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Artists._ID))))
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)))
                        .build();
                artists.add(mediaMetadataCompat);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return artists.iterator();
    }

}
