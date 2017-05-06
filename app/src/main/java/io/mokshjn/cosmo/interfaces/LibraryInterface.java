package io.mokshjn.cosmo.interfaces;

import java.util.ArrayList;

import io.mokshjn.cosmo.models.Album;
import io.mokshjn.cosmo.models.Artist;

/**
 * Created by moksh on 6/3/17.
 */

public class LibraryInterface {

    public interface onLoadAlbums {
        void setAlbums(ArrayList<Album> albums);
    }

    public interface onLoadArtists {
        void setArtists(ArrayList<Artist> artists);
    }
}
