package io.mokshjn.cosmo.loader;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.util.ArrayList;

import io.mokshjn.cosmo.interfaces.LibraryInterface;
import io.mokshjn.cosmo.models.Album;

/**
 * Created by moksh on 6/3/17.
 */

public class AlbumListLoader extends AsyncTask<Void, Album, ArrayList<Album>> {

    private ArrayList<Album> albums;
    private LibraryInterface.onLoadAlbums libraryInterface;
    private ContentResolver resolver;

    public AlbumListLoader(LibraryInterface.onLoadAlbums libraryInterface, ContentResolver resolver){
        this.libraryInterface = libraryInterface;
        this.resolver = resolver;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        albums = new ArrayList<>();
    }

    @Override
    protected ArrayList<Album> doInBackground(Void... voids) {
        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String sortOrder = MediaStore.Audio.AlbumColumns.ALBUM + " COLLATE LOCALIZED ASC";
        Cursor cursor = resolver.query(uri, null, null, null, sortOrder);

        if (cursor != null && cursor.moveToFirst()){
            do {
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
                long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Albums._ID));
                Album album1 = new Album(album, artist, albumId);
                publishProgress(album1);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return albums;
    }

    @Override
    protected void onProgressUpdate(Album... values) {
        super.onProgressUpdate(values);
        albums.add(values[0]);
    }

    @Override
    protected void onPostExecute(ArrayList<Album> alba) {
        super.onPostExecute(alba);
        libraryInterface.setAlbums(albums);
    }
}
