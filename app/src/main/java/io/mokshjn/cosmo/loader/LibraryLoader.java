package io.mokshjn.cosmo.loader;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;

import io.mokshjn.cosmo.models.Album;
import io.mokshjn.cosmo.models.Artist;
import io.mokshjn.cosmo.models.Song;

public class LibraryLoader {
    private ContentResolver resolver;

    public LibraryLoader(ContentResolver resolver) {
        this.resolver = resolver;
    }

    public ArrayList<Song> getSongsByAlbumId(long albumId) {
        ArrayList<Song> albumSongs = new ArrayList<>();
        String selection = "is_music != 0 and album_id = " + albumId;
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = resolver.query(uri, null, selection, null, null );
        if(cursor != null && cursor.moveToFirst()){
            do {
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                long albumId1 = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                Song song = new Song(id, title, artist,albumId1, album);
                albumSongs.add(song);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return albumSongs;
    }
}
