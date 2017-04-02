package io.mokshjn.cosmo.activities;

import android.content.ComponentName;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.mokshjn.cosmo.R;
import io.mokshjn.cosmo.adapters.SongAdapter;
import io.mokshjn.cosmo.helpers.LogHelper;
import io.mokshjn.cosmo.helpers.QueueHelper;
import io.mokshjn.cosmo.interfaces.MediaBrowserProvider;
import io.mokshjn.cosmo.provider.LibraryProvider;
import io.mokshjn.cosmo.services.MusicService;
import io.mokshjn.cosmo.utils.LibUtils;

/**
 * Created by moksh on 22/3/17.
 */

public class AlbumActivity extends AppCompatActivity implements MediaBrowserProvider, SongAdapter.ClickListener {

    private static final String TAG = LogHelper.makeLogTag(AlbumActivity.class);
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
    @BindView(R.id.ivAlbumArt)
    ImageView ivAlbumArt;
    @BindView(R.id.rvAlbumSongs)
    RecyclerView rvAlbumSongs;
    @BindView(R.id.line_one)
    TextView tvAlbumName;
    @BindView(R.id.line_two)
    TextView tvAlbumArtist;
    @BindView(R.id.headerView)
    View headerView;

    float headerTranslation;
    float headerImageTranslation;
    private long albumID;
    private SongAdapter adapter;
    private LibraryProvider libraryProvider;
    private String album;
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
        setContentView(R.layout.activity_album_details);
        ButterKnife.bind(this);
        mediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, MusicService.class), mConnectionCallback, null);
        Intent intent = getIntent();
        if (intent != null) {
            albumID = intent.getLongExtra("albumID", 0);
            album = LibUtils.getAlbumByAlbumId(albumID, getContentResolver());
        }
        setupToolbar();
        adapter = new SongAdapter();
        rvAlbumSongs.setLayoutManager(new LinearLayoutManager(this));
        rvAlbumSongs.setAdapter(adapter);
        adapter.setClickListener(this);
        libraryProvider = new LibraryProvider(getContentResolver());
        libraryProvider.retrieveMediaAsync(new LibraryProvider.Callback() {
            @Override
            public void onMusicLoaded(boolean success) {
                setupSongs();
            }
        });

        headerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                headerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                AlbumActivity.this.headerView.setMinimumHeight(headerView.getHeight());
            }
        });

        rvAlbumSongs.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                headerTranslation = headerView.getTranslationY() - dy;
                headerImageTranslation = ivAlbumArt.getTranslationY() + dy / 2;

                //Fixes an issue where the image translation gets a little out of sync with
                //the header translation.
                if (headerTranslation == 0) {
                    headerImageTranslation = 0;
                }

                float ratio = Math.min(1, -headerTranslation / headerView.getHeight());

                headerView.setTranslationY(headerTranslation);
                ivAlbumArt.setTranslationY(headerImageTranslation);
            }
        });

        headerView.setTranslationY(headerTranslation);
        ivAlbumArt.setTranslationY(headerImageTranslation);
    }


    private void setupSongs() {
        for (MediaSessionCompat.QueueItem item : QueueHelper.getPlayingQueueFromAlbum(album, libraryProvider)) {
            tracks.add(new MediaBrowserCompat.MediaItem(item.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
        }
        if (tracks != null && tracks.size() > 0) {
            adapter.setTracks(tracks);
            adapter.notifyDataSetChanged();
        }
        tvAlbumArtist.setText(tracks.get(0).getDescription().getSubtitle());
    }

    private void setupToolbar() {
//        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.avToolbar);
//        setSupportActionBar(toolbar);
        tvAlbumName.setText(album);
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
        return mediaBrowser;
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
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

    private void onConnected() {

    }

    @Override
    public void onSongClick(View v, int position) {
        getSupportMediaController().getTransportControls().playFromMediaId(tracks.get(position).getMediaId(), null);
    }
}
