package io.mokshjn.cosmo.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.mokshjn.cosmo.R;
import io.mokshjn.cosmo.activities.PlaylistActivity;
import io.mokshjn.cosmo.adapters.ArtistAdapter;
import io.mokshjn.cosmo.helpers.LogHelper;
import io.mokshjn.cosmo.helpers.MediaIDHelper;

/**
 * Created by moksh on 25/7/17.
 */

public class PlaylistFragment extends android.support.v4.app.Fragment implements ArtistAdapter.ClickListener {
    private static final String TAG = LogHelper.makeLogTag(ArtistFragment.class);

    private static final String ARG_MEDIA_ID = "media_id";

    private FastScrollRecyclerView rvPlaylists;
    private ArrayList<MediaBrowserCompat.MediaItem> playlists = new ArrayList<>();
    private ArtistAdapter adapter;

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
                    Log.d(TAG, parentId);
                    try {
                        playlists.clear();
                        playlists.addAll(children);
                        adapter.setArtists(playlists);
                        adapter.notifyDataSetChanged();
                    } catch (Throwable t) {
                        LogHelper.e(TAG, "Error on childrenloaded", t);
                    }
                }

                @Override
                public void onError(@NonNull String id) {
                    LogHelper.e(TAG, "browse fragment subscription onError, id=" + id);
                    Toast.makeText(getActivity(), R.string.error_loading_media, Toast.LENGTH_LONG).show();
                }
            };
    private String mMediaId;
    private SongsFragment.MediaFragmentListener mMediaFragmentListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        mMediaFragmentListener = (SongsFragment.MediaFragmentListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_song_list, container, false);
        rvPlaylists = (FastScrollRecyclerView) rootView.findViewById(R.id.rvSongList);
        rvPlaylists.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ArtistAdapter(getContext());
        rvPlaylists.setAdapter(adapter);
        adapter.setClickListener(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        MediaBrowserCompat mediaBrowser = mMediaFragmentListener.getMediaBrowser();

        if (mediaBrowser.isConnected()) {
            onConnected();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // fetch browsing information to fill the listview:
        MediaBrowserCompat mediaBrowser = mMediaFragmentListener.getMediaBrowser();

        if (mediaBrowser.isConnected()) {
            onConnected();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        MediaBrowserCompat mediaBrowser = mMediaFragmentListener.getMediaBrowser();
        if (mediaBrowser != null && mediaBrowser.isConnected() && mMediaId != null) {
            mediaBrowser.unsubscribe(mMediaId);
        }
        MediaControllerCompat controller = getActivity()
                .getSupportMediaController();
        if (controller != null) {
            controller.unregisterCallback(mMediaControllerCallback);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMediaFragmentListener = null;
    }

    public String getMediaId() {
        Bundle args = getArguments();
        if (args != null) {
            return args.getString(ARG_MEDIA_ID);
        }
        return null;
    }

    public void onConnected() {
        if (isDetached()) {
            return;
        }

        mMediaId = MediaIDHelper.MEDIA_ID_MUSICS_BY_PLAYLIST;

        mMediaFragmentListener.getMediaBrowser().unsubscribe(mMediaId);

        mMediaFragmentListener.getMediaBrowser().subscribe(mMediaId, mSubscriptionCallback);

        // Add MediaController callback so we can redraw the list when metadata changes:
        MediaControllerCompat controller = getActivity()
                .getSupportMediaController();
        if (controller != null) {
            controller.registerCallback(mMediaControllerCallback);
        }
    }

    @Override
    public void onArtistClick(View v, int position) {
        Intent intent = new Intent(getActivity(), PlaylistActivity.class);
        intent.putExtra("playlistID", playlists.get(position).getMediaId());
        startActivity(intent);
    }
}