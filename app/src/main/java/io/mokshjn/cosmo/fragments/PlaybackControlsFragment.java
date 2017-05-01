package io.mokshjn.cosmo.fragments;

import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import io.mokshjn.cosmo.R;
import io.mokshjn.cosmo.activities.FullScreenPlayerActivity;
import io.mokshjn.cosmo.activities.MainActivity;
import io.mokshjn.cosmo.helpers.LogHelper;
import io.mokshjn.cosmo.services.MusicService;

/**
 * Created by moksh on 19/3/17.
 */
public class PlaybackControlsFragment extends Fragment {

    private static final String TAG = LogHelper.makeLogTag(PlaybackControlsFragment.class);
    private final View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MediaControllerCompat controller = ((FragmentActivity) getActivity())
                    .getSupportMediaController();
            PlaybackStateCompat stateObj = controller.getPlaybackState();
            final int state = stateObj == null ?
                    PlaybackStateCompat.STATE_NONE : stateObj.getState();
            LogHelper.d(TAG, "Button pressed, in state " + state);
            switch (v.getId()) {
                case R.id.play_pause:
                    LogHelper.d(TAG, "Play button pressed, in state " + state);
                    if (state == PlaybackStateCompat.STATE_PAUSED ||
                            state == PlaybackStateCompat.STATE_STOPPED ||
                            state == PlaybackStateCompat.STATE_NONE) {
                        playMedia();
                    } else if (state == PlaybackStateCompat.STATE_PLAYING ||
                            state == PlaybackStateCompat.STATE_BUFFERING ||
                            state == PlaybackStateCompat.STATE_CONNECTING) {
                        pauseMedia();
                    }
                    break;
            }
        }
    };
    private ImageButton mPlayPause;
    private TextView mTitle;
    private TextView mSubtitle;
    private TextView mExtraInfo;
    private ImageView mAlbumArt;
    // Receive callbacks from the MediaController. Here we update our state such as which queue
    // is being shown, the current title and description and the PlaybackState.
    private final MediaControllerCompat.Callback mCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
            LogHelper.d(TAG, "Received playback state change to state ", state.getState());
            PlaybackControlsFragment.this.onPlaybackStateChanged(state);
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            if (metadata == null) {
                return;
            }
            LogHelper.d(TAG, "Received metadata state change to mediaId=",
                    metadata.getDescription().getMediaId(),
                    " song=", metadata.getDescription().getTitle());
            PlaybackControlsFragment.this.onMetadataChanged(metadata);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_playback_controls, container, false);

        mPlayPause = (ImageButton) rootView.findViewById(R.id.play_pause);
        mPlayPause.setEnabled(true);
        mPlayPause.setOnClickListener(mButtonListener);

        mTitle = (TextView) rootView.findViewById(R.id.title);
        mSubtitle = (TextView) rootView.findViewById(R.id.artist);
        mExtraInfo = (TextView) rootView.findViewById(R.id.extra_info);
        mAlbumArt = (ImageView) rootView.findViewById(R.id.album_art);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FullScreenPlayerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                MediaControllerCompat controller = ((FragmentActivity) getActivity())
                        .getSupportMediaController();
                MediaMetadataCompat metadata = controller.getMetadata();
                if (metadata != null) {
                    intent.putExtra(MainActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION,
                            metadata.getDescription());
                }
                startActivity(intent,
                        ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                                Pair.create(rootView.findViewById(R.id.album_art), getString(R.string.player_art)),
                                Pair.create(rootView.findViewById(R.id.title), getString(R.string.player_song)),
                                Pair.create(rootView.findViewById(R.id.artist), getString(R.string.player_artist))).toBundle());
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        LogHelper.d(TAG, "fragment.onStart");
        MediaControllerCompat controller = ((FragmentActivity) getActivity())
                .getSupportMediaController();
        if (controller != null) {
            onConnected();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        LogHelper.d(TAG, "fragment.onStop");
        MediaControllerCompat controller = ((FragmentActivity) getActivity())
                .getSupportMediaController();
        if (controller != null) {
            controller.unregisterCallback(mCallback);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onConnected() {
        MediaControllerCompat controller = ((FragmentActivity) getActivity())
                .getSupportMediaController();
        LogHelper.d(TAG, "onConnected, mediaController==null? ", controller == null);
        if (controller != null) {
            onMetadataChanged(controller.getMetadata());
            onPlaybackStateChanged(controller.getPlaybackState());
            controller.registerCallback(mCallback);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void onMetadataChanged(MediaMetadataCompat metadata) {
        LogHelper.d(TAG, "onMetadataChanged ", metadata);
        if (getActivity() == null) {
            LogHelper.w(TAG, "onMetadataChanged called when getActivity null," +
                    "this should not happen if the callback was properly unregistered. Ignoring.");
            return;
        }
        if (metadata == null) {
            return;
        }

        mTitle.setText(metadata.getDescription().getTitle());
        mSubtitle.setText(metadata.getDescription().getSubtitle());
        Glide.with(getContext())
                .load(metadata.getDescription().getIconUri())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .crossFade()
                .into(mAlbumArt);
    }

    public void setExtraInfo(String extraInfo) {
        if (extraInfo == null) {
            mExtraInfo.setVisibility(View.GONE);
        } else {
            mExtraInfo.setText(extraInfo);
            mExtraInfo.setVisibility(View.VISIBLE);
        }
    }

    private void onPlaybackStateChanged(PlaybackStateCompat state) {
        LogHelper.d(TAG, "onPlaybackStateChanged ", state);
        if (getActivity() == null) {
            LogHelper.w(TAG, "onPlaybackStateChanged called when getActivity null," +
                    "this should not happen if the callback was properly unregistered. Ignoring.");
            return;
        }
        if (state == null) {
            return;
        }
        boolean enablePlay = false;
        switch (state.getState()) {
            case PlaybackStateCompat.STATE_PAUSED:
            case PlaybackStateCompat.STATE_STOPPED:
                enablePlay = true;
                break;
            case PlaybackStateCompat.STATE_ERROR:
                LogHelper.e(TAG, "error playbackstate: ", state.getErrorMessage());
                Toast.makeText(getActivity(), state.getErrorMessage(), Toast.LENGTH_LONG).show();
                break;
        }

        if (enablePlay) {
            AnimatedVectorDrawable pauseToPlay = (AnimatedVectorDrawable) getActivity().getDrawable(R.drawable.avd_pause_to_play);
            mPlayPause.setImageDrawable(pauseToPlay);
            pauseToPlay.start();
        } else {
            AnimatedVectorDrawable playToPause = (AnimatedVectorDrawable) getActivity().getDrawable(R.drawable.avd_play_to_pause);
            mPlayPause.setImageDrawable(playToPause);
            playToPause.start();
        }

        MediaControllerCompat controller = ((FragmentActivity) getActivity())
                .getSupportMediaController();
        String extraInfo = null;
        if (controller != null && controller.getExtras() != null) {
            String castName = controller.getExtras().getString(MusicService.EXTRA_CONNECTED_CAST);
            if (castName != null) {
                extraInfo = getResources().getString(R.string.casting_to_device, castName);
            }
        }
        setExtraInfo(extraInfo);
    }

    private void playMedia() {
        MediaControllerCompat controller = ((FragmentActivity) getActivity())
                .getSupportMediaController();
        if (controller != null) {
            controller.getTransportControls().play();
        }
    }

    private void pauseMedia() {
        MediaControllerCompat controller = ((FragmentActivity) getActivity())
                .getSupportMediaController();
        if (controller != null) {
            controller.getTransportControls().pause();
        }
    }
}
