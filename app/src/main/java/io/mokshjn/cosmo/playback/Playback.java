package io.mokshjn.cosmo.playback;

import android.support.v4.media.session.MediaSessionCompat;

/**
 * Created by moksh on 19/3/17.
 */

public interface Playback {
    void start();

    void stop(boolean notifyListeners);

    int getState();

    void setState(int state);

    boolean isConnected();

    boolean isPlaying();

    int getCurrentStreamPosition();

    void setCurrentStreamPosition(int pos);

    void updateLastKnownStreamPosition();

    void play(MediaSessionCompat.QueueItem item);

    void pause();

    void seekTo(int position);

    String getCurrentMediaId();

    void setCurrentMediaId(String mediaId);

    void setCallback(Callback callback);

    interface Callback {

        void onCompletion();

        void onPlaybackStatusChanged(int state);

        void onError(String error);

        void setCurrentMediaId(String mediaId);
    }
}
