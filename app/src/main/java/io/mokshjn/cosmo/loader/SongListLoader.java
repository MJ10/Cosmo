package io.mokshjn.cosmo.loader;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.util.ArrayList;

import io.mokshjn.cosmo.interfaces.LibraryInterface;
import io.mokshjn.cosmo.models.Song;

/**
 * Created by moksh on 6/3/17.
 */

public class SongListLoader extends AsyncTask<Void, Song, ArrayList<Song>> {

    private ArrayList<Song> songs;
    private LibraryInterface.onLoadSongs libraryInterface;
    private ContentResolver resolver;

    public SongListLoader(LibraryInterface.onLoadSongs libraryInterface, ContentResolver resolver) {
        this.libraryInterface = libraryInterface;
        this.resolver = resolver;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        songs = new ArrayList<>();
    }

    @Override
    protected ArrayList<Song> doInBackground(Void... voids) {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String sortOrder = MediaStore.Audio.AudioColumns.TITLE + " COLLATE LOCALIZED ASC";
        Cursor cursor = resolver.query(uri, null, null, null, sortOrder);
        if(cursor != null && cursor.moveToFirst()) {
            do {
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                Song song = new Song(id, title, artist,albumId, album);
                publishProgress(song);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return songs;
    }

    @Override
    protected void onProgressUpdate(Song... values) {
        this.songs.add(values[0]);
    }

    @Override
    protected void onPostExecute(ArrayList<Song> songs) {
        super.onPostExecute(songs);
        libraryInterface.setSongs(songs);
    }
}
