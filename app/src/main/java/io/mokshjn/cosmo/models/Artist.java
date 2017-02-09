package io.mokshjn.cosmo.models;

/**
 * Created by moksh on 9/2/17.
 */

public class Artist {
    String name;
    int songsNo, albumsNo;
    long id;

    public Artist(String name, int songsNo, int albumsNo, long id) {
        this.name = name;
        this.songsNo = songsNo;
        this.albumsNo = albumsNo;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSongsNo() {
        return songsNo;
    }

    public void setSongsNo(int songsNo) {
        this.songsNo = songsNo;
    }

    public int getAlbumsNo() {
        return albumsNo;
    }

    public void setAlbumsNo(int albumsNo) {
        this.albumsNo = albumsNo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
