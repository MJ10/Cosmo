package io.mokshjn.cosmo.loader;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.util.ArrayList;

import io.mokshjn.cosmo.interfaces.LibraryInterface;
import io.mokshjn.cosmo.models.Artist;

/**
 * Created by moksh on 7/3/17.
 */

public class ArtistListLoader extends AsyncTask<Void, Artist, ArrayList<Artist>> {

    private ArrayList<Artist> artists;
    private ContentResolver resolver;
    private LibraryInterface.onLoadArtists libraryInterface;

    public ArtistListLoader(LibraryInterface.onLoadArtists libraryInterface, ContentResolver resolver) {
        this.resolver = resolver;
        this.libraryInterface = libraryInterface;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        artists = new ArrayList<>();
    }

    @Override
    protected ArrayList<Artist> doInBackground(Void... voids) {
        Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        String sortOrder = MediaStore.Audio.ArtistColumns.ARTIST + " COLLATE LOCALIZED ASC";
        Cursor cursor = resolver.query(uri, null, null, null, sortOrder);

        if(cursor != null && cursor.moveToFirst()){
            do {
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST));
                int songNo = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS));
                int albumsNo = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS));
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Artists._ID));

                Artist artist = new Artist(name, songNo, albumsNo, id);
                publishProgress(artist);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return artists;
    }

    @Override
    protected void onProgressUpdate(Artist... values) {
        artists.add(values[0]);
    }

    @Override
    protected void onPostExecute(ArrayList<Artist> artists) {
        super.onPostExecute(artists);
        libraryInterface.setArtists(artists);
    }
}
