package io.mokshjn.cosmo.models;

import java.io.Serializable;

/**
 * Created by moksh on 1/2/17.
 */

public class Album implements Serializable {
    String albumTitle, artist;
    long albumId;

    public Album(String albumTitle, String artist, long albumId) {
        this.albumTitle = albumTitle;
        this.artist = artist;
        this.albumId = albumId;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }
}
