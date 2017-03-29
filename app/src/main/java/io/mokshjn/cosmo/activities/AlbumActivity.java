package io.mokshjn.cosmo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.mokshjn.cosmo.R;
import io.mokshjn.cosmo.adapters.SongAdapter;
import io.mokshjn.cosmo.helpers.LogHelper;
import io.mokshjn.cosmo.interfaces.MediaBrowserProvider;
import io.mokshjn.cosmo.provider.LibraryProvider;
import io.mokshjn.cosmo.utils.LibUtils;

/**
 * Created by moksh on 22/3/17.
 */

public class AlbumActivity extends AppCompatActivity implements MediaBrowserProvider {

    private static final String TAG = LogHelper.makeLogTag(AlbumActivity.class);
    private final MediaControllerCompat.Callback mMediaControllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {

                }

                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {

                }
            };
    @BindView(R.id.ivAlbumArt)
    ImageView ivAlbumArt;
    private long albumID;
    private SongAdapter adapter;
    private LibraryProvider libraryProvider;
    private String searchQuery;
    private MediaBrowserCompat mediaBrowser;
    private final MediaBrowserCompat.ConnectionCallback mConnectionCallback =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    LogHelper.d(TAG, "onConnected");
                    try {
                        connectToSession(mediaBrowser.getSessionToken());
                    } catch (RemoteException e) {
                        LogHelper.e(TAG, e, "could not connect media controller");
                    }
                }
            };
    private ArrayList<MediaBrowserCompat.MediaItem> tracks = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_view_activity);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        if (intent != null) {
            albumID = intent.getLongExtra("albumID", 0);
        }
        setupToolbar();
    }

    private void setupToolbar() {
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.avToolbar);
        setSupportActionBar(toolbar);

        Glide.with(this)
                .load(LibUtils.getMediaStoreAlbumCoverUri(albumID))
                .crossFade()
                .into(ivAlbumArt);
    }

    private void connectToSession(MediaSessionCompat.Token token) throws RemoteException {
        MediaControllerCompat mediaController = new MediaControllerCompat(this, token);
        setSupportMediaController(mediaController);
        mediaController.registerCallback(mMediaControllerCallback);
    }

    @Override
    public MediaBrowserCompat getMediaBrowser() {
        return null;
    }
}
