package io.mokshjn.cosmo.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.mokshjn.cosmo.R;
import io.mokshjn.cosmo.adapters.AlbumSongsAdapter;
import io.mokshjn.cosmo.helpers.LogHelper;
import io.mokshjn.cosmo.helpers.QueueHelper;
import io.mokshjn.cosmo.interfaces.MediaBrowserProvider;
import io.mokshjn.cosmo.provider.LibraryProvider;
import io.mokshjn.cosmo.services.MusicService;
import io.mokshjn.cosmo.utils.LibUtils;

/**
 * Created by moksh on 22/3/17.
 */

public class AlbumActivity extends AppCompatActivity implements MediaBrowserProvider, AlbumSongsAdapter.ClickListener {

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
    @BindView(R.id.scrollView)
    NestedScrollView scrollView;

    private long albumID;
    private AlbumSongsAdapter adapter;
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
        setContentView(R.layout.activity_album);
        ButterKnife.bind(this);
        setEnterTransitions();
        mediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, MusicService.class), mConnectionCallback, null);
        Intent intent = getIntent();
        if (intent != null) {
            albumID = intent.getLongExtra("albumID", 0);
            album = LibUtils.getAlbumByAlbumId(albumID, getContentResolver());
        }
        setupToolbar();
        adapter = new AlbumSongsAdapter();
        rvAlbumSongs.setLayoutManager(new LinearLayoutManager(this));
        rvAlbumSongs.setAdapter(adapter);
        adapter.setClickListener(this);
        rvAlbumSongs.setNestedScrollingEnabled(false);
        libraryProvider = new LibraryProvider(getContentResolver());
        libraryProvider.retrieveMediaAsync(new LibraryProvider.Callback() {
            @Override
            public void onMusicLoaded(boolean success) {
                setupSongs();
            }
        });
    }

    private void setEnterTransitions() {
        Slide slide = new Slide(Gravity.BOTTOM);
        slide.addTarget(R.id.rvAlbumList);
        slide.addTarget(R.id.bgView);
        slide.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in));
        slide.setDuration(450);
        getWindow().setEnterTransition(slide);
        getWindow().setExitTransition(slide);
    }

    @Override
    public void onEnterAnimationComplete() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rvAlbumSongs.setVisibility(View.VISIBLE);
            }
        }, 500);
    }

    private void setupSongs() {
        for (MediaSessionCompat.QueueItem item : QueueHelper.getPlayingQueueFromAlbum(album, libraryProvider)) {
            tracks.add(new MediaBrowserCompat.MediaItem(item.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
        }
        if (tracks != null && tracks.size() > 0) {
            adapter.setTracks(tracks);
            adapter.notifyDataSetChanged();
        }
    }

    private void setupToolbar() {
        setupAlbumArt();
    }

    private void setupAlbumArt() {
        Glide.with(this)
                .load(LibUtils.getMediaStoreAlbumCoverUri(albumID))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
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
        rvAlbumSongs.setVisibility(View.GONE);
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
