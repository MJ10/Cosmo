package io.mokshjn.cosmo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import io.mokshjn.cosmo.R;
import io.mokshjn.cosmo.adapters.SongAdapter;
import io.mokshjn.cosmo.fragments.SongsFragment;
import io.mokshjn.cosmo.helpers.LogHelper;
import io.mokshjn.cosmo.interfaces.MediaBrowserProvider;
import io.mokshjn.cosmo.utils.LibUtils;
import io.mokshjn.cosmo.widgets.ParallaxScrimageView;
import xyz.klinker.android.drag_dismiss.activity.DragDismissActivity;

/**
 * Created by moksh on 22/3/17.
 */

public class AlbumActivity extends DragDismissActivity {

    private static final String TAG = LogHelper.makeLogTag(AlbumActivity.class);

    private long albumID;
    private ParallaxScrimageView ivAlbumArt;

    private ArrayList<MediaBrowserCompat.MediaItem> tracks;
    private SongAdapter adapter;
    private final MediaControllerCompat.Callback mMediaControllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    super.onMetadataChanged(metadata);
                    if (metadata == null) {
                        return;
                    }
                    LogHelper.d(TAG, "Received metadata change to media ",
                            metadata.getDescription().getMediaId());
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
                    super.onPlaybackStateChanged(state);
                    LogHelper.d(TAG, "Received state change: ", state);
                    adapter.notifyDataSetChanged();
                }
            };
    private final MediaBrowserCompat.SubscriptionCallback mSubscriptionCallback =
            new MediaBrowserCompat.SubscriptionCallback() {
                @Override
                public void onChildrenLoaded(@NonNull String parentId,
                                             @NonNull List<MediaBrowserCompat.MediaItem> children) {
                    try {
                        LogHelper.d(TAG, "fragment onChildrenLoaded, parentId=" + parentId +
                                "  count=" + children.size());
                        tracks.clear();
                        for (MediaBrowserCompat.MediaItem item : children) {
                            tracks.add(item);
                        }

                        adapter.notifyDataSetChanged();
                    } catch (Throwable t) {
                        LogHelper.e(TAG, "Error on childrenloaded", t);
                    }
                }

                @Override
                public void onError(@NonNull String id) {
                    LogHelper.e(TAG, "browse fragment subscription onError, id=" + id);
                    Toast.makeText(AlbumActivity.this, R.string.error_loading_media, Toast.LENGTH_LONG).show();
                }
            };
    private SongsFragment.MediaFragmentListener mMediaFragmentlistener;

    @Override
    protected View onCreateContent(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.album_view_activity, parent, false);
        ButterKnife.bind(this);
        Intent callingIntent = getIntent();
        if (callingIntent != null) {
            albumID = callingIntent.getLongExtra("albumID", 0);
        }
        setupViews(v);
        return v;
    }

    private void setupViews(View v) {
        ivAlbumArt = (ParallaxScrimageView) v.findViewById(R.id.ivAlbumArt);
        ivAlbumArt.setImmediatePin(true);
        Glide.with(this)
                .loadFromMediaStore(LibUtils.getMediaStoreAlbumCoverUri(albumID))
                .override(600, 500)
                .into(ivAlbumArt);


    }

    @Override
    protected void onStart() {
        super.onStart();
        tracks = new ArrayList<>();
        // fetch browsing information to fill the listview:
//        MediaBrowserCompat mediaBrowser = mMediaFragmentlistener.getMediaBrowser();

//        if (mediaBrowser.isConnected()) {
//            onConnected();
//        }
    }

    public interface MediaFragmentListener extends MediaBrowserProvider {
    }
}
