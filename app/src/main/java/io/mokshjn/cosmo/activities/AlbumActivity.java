package io.mokshjn.cosmo.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.ImageViewTarget;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.mokshjn.cosmo.R;
import io.mokshjn.cosmo.adapters.AlbumSongsAdapter;
import io.mokshjn.cosmo.helpers.LogHelper;
import io.mokshjn.cosmo.helpers.MediaIDHelper;
import io.mokshjn.cosmo.interfaces.MediaBrowserProvider;
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
    @BindView(R.id.bgView)
    View bgView;

    private long albumID;
    private String mediaID;
    private AlbumSongsAdapter adapter;
    private MediaBrowserCompat mediaBrowser;
    private final MediaBrowserCompat.ConnectionCallback mConnectionCallback =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    Log.d(TAG, "onConnected: ");
                    try {
                        connectToSession(mediaBrowser.getSessionToken());
                    } catch (RemoteException e) {
                        LogHelper.e(TAG, e, "could not connect media controller");
                    }
                }
            };
    private Palette.PaletteAsyncListener listener = new Palette.PaletteAsyncListener() {
        @Override
        public void onGenerated(Palette palette) {
            int defaultColor = getResources().getColor(R.color.colorPrimary);
            int lightMutedColor = palette.getLightMutedColor(defaultColor);
            int darkVibrantColor = palette.getDarkVibrantColor(defaultColor);
            bgView.setBackgroundColor(lightMutedColor);
            adapter.setBgColor(darkVibrantColor);
            adapter.notifyDataSetChanged();
        }
    };
    private ArrayList<MediaBrowserCompat.MediaItem> tracks = new ArrayList<>();
    private final MediaBrowserCompat.SubscriptionCallback mSubscriptionCallback =
            new MediaBrowserCompat.SubscriptionCallback() {
                @Override
                public void onChildrenLoaded(@NonNull String parentId,
                                             @NonNull List<MediaBrowserCompat.MediaItem> children) {
                    Log.d(TAG, parentId);
                    try {
                        tracks.clear();
                        for (MediaBrowserCompat.MediaItem item : children) {
                            tracks.add(item);
                        }
                        adapter.setTracks(tracks);
                        adapter.notifyDataSetChanged();
                    } catch (Throwable t) {
                        LogHelper.e(TAG, "Error on childrenloaded", t);
                    }
                }

                @Override
                public void onError(@NonNull String id) {
                    LogHelper.e(TAG, "browse fragment subscription onError, id=" + id);
                    Toast.makeText(AlbumActivity.this, getString(R.string.error_loading_media), Toast.LENGTH_LONG).show();
                }
            };

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
            mediaID = intent.getStringExtra("albumID");
            albumID = MediaIDHelper.extractAlbumID(mediaID);
        }
        setupViews();
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
        }, 450);
    }

    private void setupViews() {
        setupAlbumArt();

        adapter = new AlbumSongsAdapter(this);
        rvAlbumSongs.setLayoutManager(new LinearLayoutManager(this));
        rvAlbumSongs.setAdapter(adapter);
        adapter.setClickListener(this);
        rvAlbumSongs.setNestedScrollingEnabled(false);
    }

    private void setupAlbumArt() {
        Glide.with(this)
                .load(LibUtils.getMediaStoreAlbumCoverUri(albumID))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(new ImageViewTarget<GlideDrawable>(ivAlbumArt) {
                    @Override
                    protected void setResource(GlideDrawable resource) {
                        ivAlbumArt.setImageDrawable(resource.getCurrent());

                        extractColors(resource);
                    }
                });
    }

    private void extractColors(GlideDrawable resource) {
        Bitmap bitmap = ((GlideBitmapDrawable) resource.getCurrent()).getBitmap();
        Palette.from(bitmap).generate(listener);
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

        Log.d(TAG, "onConnected: " + mediaID);

        mediaBrowser.unsubscribe(mediaID);

        mediaBrowser.subscribe(mediaID, mSubscriptionCallback);
    }

    @Override
    public void onSongClick(View v, int position) {
        getSupportMediaController().getTransportControls().playFromMediaId(tracks.get(position).getMediaId(), null);
    }
}
