package io.mokshjn.cosmo.loader;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.HashMap;

import io.mokshjn.cosmo.models.Album;
import io.mokshjn.cosmo.models.Song;

public class LibraryLoader {
    private ContentResolver resolver;

    public LibraryLoader(ContentResolver resolver) {
        this.resolver = resolver;
    }

    public ArrayList<Album> getAlbumList(){
        ArrayList<Album> albumList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor cursor = resolver.query(uri, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()){

            int artistColumn = cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST);
            int albumColumn = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
            int albumIdColumn = cursor.getColumnIndex(MediaStore.Audio.Albums._ID);

            do {
                String artist = cursor.getString(artistColumn);
                String album = cursor.getString(albumColumn);
                long albumId = cursor.getLong(albumIdColumn);
                Album album1 = new Album(album, artist, albumId);
                albumList.add(album1);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return albumList;
    }

    public ArrayList<Song> getSongList() {
        ArrayList<Song> songList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = resolver.query(uri, null, null, null, null);
        if(cursor != null && cursor.moveToFirst()) {
            int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int albumIdColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);

            do {
                String artist = cursor.getString(artistColumn);
                String album = cursor.getString(albumColumn);
                String title = cursor.getString(titleColumn);
                long id = cursor.getLong(idColumn);
                long albumId = cursor.getLong(albumIdColumn);
                Song song = new Song(id, title, artist,albumId, album);
                songList.add(song);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return songList;
    }
}
