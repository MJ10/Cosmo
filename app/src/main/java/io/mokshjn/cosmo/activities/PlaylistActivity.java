package io.mokshjn.cosmo.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.mokshjn.cosmo.R;
import io.mokshjn.cosmo.adapters.AlbumSongsAdapter;
import io.mokshjn.cosmo.helpers.LogHelper;
import io.mokshjn.cosmo.interfaces.MediaBrowserProvider;
import io.mokshjn.cosmo.services.MusicService;

public class PlaylistActivity extends AppCompatActivity implements MediaBrowserProvider, AlbumSongsAdapter.ClickListener {

    private static final String TAG = LogHelper.makeLogTag(PlaylistActivity.class);
    private final MediaControllerCompat.Callback mMediaControllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
                    super.onPlaybackStateChanged(state);
                }

                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    super.onMetadataChanged(metadata);
                }
            };

    @BindView(R.id.rvPlaylistSongs)
    RecyclerView rvPlaylistSongs;

    private String mediaID;
    private AlbumSongsAdapter adapter;
    private MediaBrowserCompat mediaBrowser;
    private final MediaBrowserCompat.ConnectionCallback mConnectionCallback =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    try {
                        connectToSession(mediaBrowser.getSessionToken());
                    } catch (RemoteException e) {
                        LogHelper.e(TAG, e, "could not connect media controller");
                    }
                }
            };
    private ArrayList<MediaBrowserCompat.MediaItem> tracks = new ArrayList<>();
    private final MediaBrowserCompat.SubscriptionCallback mSubscriptionCallback =
            new MediaBrowserCompat.SubscriptionCallback() {
                @Override
                public void onChildrenLoaded(@NonNull String parentId,
                                             @NonNull List<MediaBrowserCompat.MediaItem> children) {
                    try {
                        tracks.clear();
                        tracks.addAll(children);
                        adapter.setTracks(tracks);
                        adapter.notifyDataSetChanged();
                    } catch (Throwable t) {
                        LogHelper.e(TAG, "Error on childrenloaded", t);
                    }
                }

                @Override
                public void onError(@NonNull String id) {
                    LogHelper.e(TAG, "browse fragment subscription onError, id=" + id);
                    Toast.makeText(PlaylistActivity.this, getString(R.string.error_loading_media), Toast.LENGTH_LONG).show();
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        ButterKnife.bind(this);

        mediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, MusicService.class), mConnectionCallback, null);
        Intent intent = getIntent();

        if (intent != null) {
            mediaID = intent.getStringExtra("playlistID");
        }

        adapter = new AlbumSongsAdapter(this);
        rvPlaylistSongs.setLayoutManager(new LinearLayoutManager(this));
        rvPlaylistSongs.setAdapter(adapter);
        adapter.setClickListener(this);
        rvPlaylistSongs.setNestedScrollingEnabled(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mediaBrowser.connect();
        if (mediaBrowser.isConnected()) {
            onConnected();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaBrowser.disconnect();
        if (getSupportMediaController() != null) {
            getSupportMediaController().unregisterCallback(mMediaControllerCallback);
        }
    }

    private void connectToSession(MediaSessionCompat.Token token) throws RemoteException {
        MediaControllerCompat mediaController = new MediaControllerCompat(this, token);
        setSupportMediaController(mediaController);
        mediaController.registerCallback(mMediaControllerCallback);

        onConnected();
    }

    @Override
    public MediaBrowserCompat getMediaBrowser() {
        return mediaBrowser;
    }

    private void onConnected() {

        mediaBrowser.unsubscribe(mediaID);

        mediaBrowser.subscribe(mediaID, mSubscriptionCallback);
    }

    @Override
    public void onSongClick(View v, int position) {

    }
}
