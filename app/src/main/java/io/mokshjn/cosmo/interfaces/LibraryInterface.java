package io.mokshjn.cosmo.interfaces;

import java.util.ArrayList;

import io.mokshjn.cosmo.models.Album;
import io.mokshjn.cosmo.models.Artist;
import io.mokshjn.cosmo.models.Song;

/**
 * Created by moksh on 6/3/17.
 */

public class LibraryInterface {

    public interface onLoadSongs {
        void setSongs(ArrayList<Song> songs);
    }

    public interface onLoadAlbums {
        void setAlbums(ArrayList<Album> albums);
    }

    public interface onLoadArtists {
        void setArtists(ArrayList<Artist> artists);
    }
}
