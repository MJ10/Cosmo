package io.mokshjn.cosmo.provider;

import android.support.v4.media.MediaMetadataCompat;

import java.util.Iterator;

/**
 * Created by moksh on 19/3/17.
 */

public interface MusicProviderSource {
    String CUSTOM_METADATA_TRACK_SOURCE = "__SOURCE__";
    Iterator<MediaMetadataCompat> iterator();
}
