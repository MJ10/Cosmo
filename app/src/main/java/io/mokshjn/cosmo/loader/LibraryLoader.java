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

    public ArrayList<Album> getAlbumList(){
        ArrayList<Album> albumList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String sortOrder = MediaStore.Audio.AlbumColumns.ALBUM + " COLLATE LOCALIZED ASC";
        Cursor cursor = resolver.query(uri, null, null, null, sortOrder);

        if (cursor != null && cursor.moveToFirst()){
            do {
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
                long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Albums._ID));
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
                songList.add(song);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return songList;
    }

    private ArrayList<Artist> getArtistList() {
        ArrayList<Artist> artistList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        String sortOrder = MediaStore.Audio.ArtistColumns.ARTIST + " COLLATE LOCALIZED ASCII";

        Cursor cursor = resolver.query(uri, null, null, null, sortOrder);
        if(cursor != null && cursor.moveToFirst()){
            do {
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST));
                int albumsNo = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS));
                int songsNo = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS));
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Artists._ID));
                Artist artist = new Artist(name, songsNo, albumsNo, id);
                artistList.add(artist);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return artistList;
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
