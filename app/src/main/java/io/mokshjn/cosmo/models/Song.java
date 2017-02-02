package io.mokshjn.cosmo.models;

import android.net.Uri;

import java.io.Serializable;

public class Song implements Serializable {
    private String artist, title, album;
    private long id, albumId;

    public Song(long id, String title, String artist, long albumId, String album) {
        this.artist = artist;
        this.title = title;
        this.album = album;
        this.id = id;
        this.albumId = albumId;
    }

    public long getAlbumId() {
        return albumId;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public long getId() {
        return id;
    }
}
