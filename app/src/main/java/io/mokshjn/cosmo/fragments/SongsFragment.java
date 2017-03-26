package io.mokshjn.cosmo.fragments;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
import io.mokshjn.cosmo.adapters.SongAdapter;
import io.mokshjn.cosmo.helpers.LogHelper;
import io.mokshjn.cosmo.interfaces.MediaBrowserProvider;

/**
 * Created by moksh on 19/3/17.
 */

public class SongsFragment extends android.support.v4.app.Fragment implements SongAdapter.ClickListener {

    private static final String TAG = LogHelper.makeLogTag(SongsFragment.class);

    private static final String ARG_MEDIA_ID = "media_id";

    private FastScrollRecyclerView rvSongList;
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
                        adapter.setTracks(tracks);
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
    private MediaFragmentListener mMediaFragmentlistener;

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        Log.d(TAG, "Initialised fragment listener");
        mMediaFragmentlistener = (MediaFragmentListener) context;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_song_list, container, false);
        rvSongList = (FastScrollRecyclerView) rootView.findViewById(R.id.rvSongList);
        rvSongList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SongAdapter();
        rvSongList.setAdapter(adapter);
        adapter.setClickListener(this);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        tracks = new ArrayList<>();
        // fetch browsing information to fill the listview:
        MediaBrowserCompat mediaBrowser = mMediaFragmentlistener.getMediaBrowser();

        if (mediaBrowser.isConnected()) {
            onConnected();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        MediaBrowserCompat mediaBrowser = mMediaFragmentlistener.getMediaBrowser();
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
        mMediaFragmentlistener = null;
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
        mMediaId = getMediaId();
        if (mMediaId == null) {
            mMediaId = mMediaFragmentlistener.getMediaBrowser().getRoot();
        }

        mMediaFragmentlistener.getMediaBrowser().unsubscribe(mMediaId);

        mMediaFragmentlistener.getMediaBrowser().subscribe(mMediaId, mSubscriptionCallback);

        // Add MediaController callback so we can redraw the list when metadata changes:
        MediaControllerCompat controller = getActivity()
                .getSupportMediaController();
        if (controller != null) {
            controller.registerCallback(mMediaControllerCallback);
        }
    }

    @Override
    public void onSongClick(View v, int position) {
        MediaBrowserCompat.MediaItem item = tracks.get(position);
        if (item.isPlayable()) {
            getActivity().getSupportMediaController().getTransportControls()
                    .playFromMediaId(item.getMediaId(), null);
        }
    }

    public interface MediaFragmentListener extends MediaBrowserProvider {
    }
}
