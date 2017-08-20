package io.mokshjn.cosmo.provider;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.mokshjn.cosmo.helpers.LogHelper;
import io.mokshjn.cosmo.helpers.MediaIDHelper;
import io.mokshjn.cosmo.models.MutableMediaMetadata;
import io.mokshjn.cosmo.utils.LibUtils;

import static io.mokshjn.cosmo.helpers.MediaIDHelper.MEDIA_ID_MUSICS_BY_ALBUM;
import static io.mokshjn.cosmo.helpers.MediaIDHelper.MEDIA_ID_MUSICS_BY_ARTIST;
import static io.mokshjn.cosmo.helpers.MediaIDHelper.MEDIA_ID_MUSICS_BY_PLAYLIST;
import static io.mokshjn.cosmo.helpers.MediaIDHelper.MEDIA_ID_ROOT;
import static io.mokshjn.cosmo.helpers.MediaIDHelper.createMediaID;

/**
 * Created by moksh on 19/3/17.
 */

public class LibraryProvider {
    private static final String TAG = "cosmo.LibraryProvider";

    private MusicProviderSource mSource;

    private ConcurrentMap<String, List<MediaMetadataCompat>> mMusicListByAlbum;
    private ConcurrentMap<String, List<MediaMetadataCompat>> mMusicListByPlaylist;
    private ConcurrentMap<String, List<MediaMetadataCompat>> mMusicListByArtist;
    private ConcurrentMap<String, MutableMediaMetadata> mMusicListById;
    private List<MediaMetadataCompat> tracks;
    private List<MediaMetadataCompat> albums;
    private List<MediaMetadataCompat> artists;
    private List<MediaMetadataCompat> playlists;
    private volatile State mCurrentState = State.NON_INITIALIZED;

    public LibraryProvider(ContentResolver resolver) {this(new LibrarySource(resolver)); }

    public LibraryProvider(MusicProviderSource source) {
        mSource = source;
        mMusicListById = new ConcurrentHashMap<>();
        mMusicListByAlbum = new ConcurrentHashMap<>();
        mMusicListByPlaylist = new ConcurrentHashMap<>();
        mMusicListByArtist = new ConcurrentHashMap<>();
        tracks = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
        playlists = new ArrayList<>();
    }

    public Iterable<MediaMetadataCompat> getShuffledMusic() {
        if (mCurrentState != State.INITIALIZED) {
            return Collections.emptyList();
        }
        List<MediaMetadataCompat> shuffled = new ArrayList<>(mMusicListById.size());
        for (MutableMediaMetadata mutableMetadata: mMusicListById.values()) {
            shuffled.add(mutableMetadata.metadata);
        }
        Collections.shuffle(shuffled);
        return shuffled;
    }

    public Iterable<MediaMetadataCompat> searchMusicBySongTitle(String query) {
        return searchMusic(MediaMetadataCompat.METADATA_KEY_TITLE, query);
    }

    public Iterable<MediaMetadataCompat> searchMusicByAlbum(String query) {
        return searchMusic(MediaMetadataCompat.METADATA_KEY_ALBUM, query);
    }

    public Iterable<MediaMetadataCompat> searchMusicByArtist(String query) {
        return searchMusic(MediaMetadataCompat.METADATA_KEY_ARTIST, query);
    }

    private Iterable<MediaMetadataCompat> searchMusic(String metadataField, String query) {
        if (mCurrentState != State.INITIALIZED) {
            return Collections.emptyList();
        }
        ArrayList<MediaMetadataCompat> result = new ArrayList<>();
        query = query.toLowerCase(Locale.US);
        if (metadataField == MediaMetadataCompat.METADATA_KEY_ALBUM) {
            for (MutableMediaMetadata track : mMusicListById.values()) {
                if (track.metadata.getString(metadataField).toLowerCase(Locale.US)
                        .equals(query)) {
                    result.add(track.metadata);
                }
            }
        } else {
            for (MutableMediaMetadata track : mMusicListById.values()) {
                if (track.metadata.getString(metadataField).toLowerCase(Locale.US)
                        .contains(query)) {
                    result.add(track.metadata);
                }
            }
        }
        return result;
    }

    public List<MediaMetadataCompat> getTracks() {
        return tracks;
    }

    public MediaMetadataCompat getMusic(String musicId) {
        return mMusicListById.containsKey(musicId) ? mMusicListById.get(musicId).metadata : null;
    }

    public synchronized void updateMusicArt(String musicId, Bitmap albumArt, Bitmap icon) {
        MediaMetadataCompat metadata = getMusic(musicId);
        metadata = new MediaMetadataCompat.Builder(metadata)

                // set high resolution bitmap in METADATA_KEY_ALBUM_ART. This is used, for
                // example, on the lockscreen background when the media session is active.
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)

                // set small version of the album art in the DISPLAY_ICON. This is used on
                // the MediaDescription and thus it should be small to be serialized if
                // necessary
                .putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, icon)

                .build();

        MutableMediaMetadata mutableMetadata = mMusicListById.get(musicId);
        if (mutableMetadata == null) {
            throw new IllegalStateException("Unexpected error: Inconsistent data structures in " +
                    "MusicProvider");
        }

        mutableMetadata.metadata = metadata;
    }

    public boolean isInitialized() {
        return mCurrentState == State.INITIALIZED;
    }

    public void retrieveMediaAsync(final Callback callback) {
        if (mCurrentState == State.INITIALIZED) {
            if (callback != null) {
                // Nothing to do, execute callback immediately
                callback.onMusicLoaded(true);
            }
            return;
        }
        // Asynchronously load the music catalog in a separate thread
        new AsyncTask<Void, Void, State>() {
            @Override
            protected State doInBackground(Void... params) {
                retrieveMedia();
                return mCurrentState;
            }

            @Override
            protected void onPostExecute(State current) {
                if (callback != null) {
                    callback.onMusicLoaded(current == State.INITIALIZED);
                }
            }
        }.execute();
    }

    private synchronized void retrieveMedia() {
        try {
            if (mCurrentState == State.NON_INITIALIZED) {
                mCurrentState = State.INITIALIZING;
                List<MediaMetadataCompat> emptyList = new ArrayList<>();
                Iterator<MediaMetadataCompat> iterator = mSource.iterator();
                while (iterator.hasNext()) {
                    MediaMetadataCompat item = iterator.next();
                    String musicId = item.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);
                    mMusicListById.put(musicId, new MutableMediaMetadata(musicId, item));
                    tracks.add(item);
                }
                iterator = mSource.albums();
                while (iterator.hasNext()) {
                    MediaMetadataCompat item = iterator.next();
                    String musicId = item.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);
                    mMusicListByAlbum.put(musicId, emptyList);
                    albums.add(item);
                }
                iterator = mSource.artists();
                while (iterator.hasNext()) {
                    MediaMetadataCompat item = iterator.next();
                    String musicId = item.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);
                    mMusicListByArtist.put(musicId, emptyList);
                    artists.add(item);
                }
                iterator = mSource.playlists();
                while (iterator.hasNext()) {
                    MediaMetadataCompat item = iterator.next();
                    String id = item.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);
                    mMusicListByPlaylist.put(id, emptyList);
                    playlists.add(item);
                }
                buildListsByAlbum();
                buildListsByArtist();
                mCurrentState = State.INITIALIZED;
            }
        } finally {
            if (mCurrentState != State.INITIALIZED) {
                // Something bad happened, so we reset state to NON_INITIALIZED to allow
                // retries (eg if the network connection is temporary unavailable)
                mCurrentState = State.NON_INITIALIZED;
            }
        }
    }

    private synchronized void buildListsByAlbum() {

        for (MutableMediaMetadata m : mMusicListById.values()) {
            String album = m.metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM);
            List<MediaMetadataCompat> list = mMusicListByAlbum.get(album);
            if (list == null) {
                list = new ArrayList<>();
                mMusicListByAlbum.put(album, list);
            }
            mMusicListByAlbum.get(album).add(m.metadata);
        }
    }

    private synchronized void buildListsByArtist() {

        for (MutableMediaMetadata m : mMusicListById.values()) {
            String artist = m.metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST);
            List<MediaMetadataCompat> list = mMusicListByArtist.get(artist);
            if (list == null) {
                list = new ArrayList<>();
                mMusicListByArtist.put(artist, list);
            }
            mMusicListByArtist.get(artist).add(m.metadata);
        }
    }

    public List<MediaBrowserCompat.MediaItem> getChildren(String mediaId) {
        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

        if (!MediaIDHelper.isBrowseable(mediaId)) {
            return mediaItems;
        }

        if (MEDIA_ID_ROOT.equals(mediaId)) {
            for (MediaMetadataCompat item : tracks) {
                mediaItems.add(createMediaItem(item, MEDIA_ID_ROOT));
            }
        } else if (mediaId.equals(MEDIA_ID_MUSICS_BY_ALBUM)) {
            for (MediaMetadataCompat item : albums) {
                mediaItems.add(createAlbumItem(item));
            }
        } else if (mediaId.equals(MEDIA_ID_MUSICS_BY_ARTIST)) {
            for (MediaMetadataCompat item : artists) {
                mediaItems.add(createArtistItem(item));
            }
        } else if (mediaId.startsWith(MEDIA_ID_MUSICS_BY_PLAYLIST)) {
            for (MediaMetadataCompat item : playlists) {
                mediaItems.add(createPlaylistItem(item));
            }
        } else if (mediaId.startsWith(MEDIA_ID_MUSICS_BY_ALBUM)) {
            Long albumID = MediaIDHelper.extractID(mediaId);
            List<MediaMetadataCompat> tracks = mMusicListByAlbum.get(albumID.toString());
            tracks.sort(new Comparator<MediaMetadataCompat>() {
                @Override
                public int compare(MediaMetadataCompat o1, MediaMetadataCompat o2) {
                    return (int) (o1.getLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER) - o2.getLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER));
                }
            });
            for (MediaMetadataCompat item : mMusicListByAlbum.get(albumID.toString())) {
                mediaItems.add(createMediaItem(item, mediaId));
            }
        } else if (mediaId.startsWith(MEDIA_ID_MUSICS_BY_PLAYLIST)) {
            long playlistId = MediaIDHelper.extractID(mediaId);
            for (MediaMetadataCompat item : mSource.getMusicFromPlaylist(playlistId)) {
                mediaItems.add(createMediaItem(item, mediaId));
            }
        } else {
            LogHelper.w(TAG, "Skipping unmatched mediaId: ", mediaId);
        }
        return mediaItems;
    }

    private MediaBrowserCompat.MediaItem createAlbumItem(MediaMetadataCompat item) {
        MediaDescriptionCompat description = new MediaDescriptionCompat.Builder()
                .setMediaId(createMediaID(null, MEDIA_ID_MUSICS_BY_ALBUM, item.getDescription().getMediaId()))
                .setTitle(item.getDescription().getTitle())
                .setSubtitle(item.getDescription().getSubtitle())
                .setIconUri(LibUtils.getMediaStoreAlbumCoverUri(Long.parseLong(item.getDescription().getMediaId())))
                .build();
        return new MediaBrowserCompat.MediaItem(description,
                MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
    }

    private MediaBrowserCompat.MediaItem createPlaylistItem(MediaMetadataCompat item) {
        MediaDescriptionCompat descriptionCompat = new MediaDescriptionCompat.Builder()
                .setMediaId(createMediaID(null, MEDIA_ID_MUSICS_BY_PLAYLIST, item.getDescription().getMediaId()))
                .setTitle(item.getDescription().getTitle())
                .setSubtitle(item.getDescription().getSubtitle())
                .build();
        return new MediaBrowserCompat.MediaItem(descriptionCompat,
                MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
    }

    private MediaBrowserCompat.MediaItem createArtistItem(MediaMetadataCompat item) {
        MediaDescriptionCompat description = new MediaDescriptionCompat.Builder()
                .setMediaId(createMediaID(null, MEDIA_ID_MUSICS_BY_ARTIST, item.getDescription().getMediaId()))
                .setTitle(item.getDescription().getTitle())
                .setSubtitle(item.getDescription().getSubtitle())
                .build();
        return new MediaBrowserCompat.MediaItem(description,
                MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
    }

    private MediaBrowserCompat.MediaItem createMediaItem(MediaMetadataCompat metadata, String id) {
        // Since mediaMetadata fields are immutable, we need to create a copy, so we
        // can set a hierarchy-aware mediaID. We will need to know the media hierarchy
        // when we get a onPlayFromMusicID call, so we can create the proper queue based
        // on where the music was selected from (by artist, by genre, random, etc)
        String hierarchyAwareMediaID = createMediaID(
                metadata.getDescription().getMediaId(), id);
        MediaMetadataCompat copy = new MediaMetadataCompat.Builder(metadata)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, hierarchyAwareMediaID)
                .build();
        return new MediaBrowserCompat.MediaItem(copy.getDescription(),
                MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);

    }

    private enum State {
        NON_INITIALIZED, INITIALIZING, INITIALIZED
    }

    public interface Callback {
        void onMusicLoaded(boolean success);
    }

}
