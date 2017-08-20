package io.mokshjn.cosmo.provider;

import android.support.v4.media.MediaMetadataCompat;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by moksh on 19/3/17.
 */

public interface MusicProviderSource {
    Iterator<MediaMetadataCompat> iterator();

    Iterator<MediaMetadataCompat> albums();

    Iterator<MediaMetadataCompat> artists();

    Iterator<MediaMetadataCompat> playlists();

    ArrayList<MediaMetadataCompat> getMusicFromPlaylist(long id);
}
