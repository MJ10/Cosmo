package io.mokshjn.cosmo.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import io.mokshjn.cosmo.R;
import io.mokshjn.cosmo.fragments.SongListFragment;
import io.mokshjn.cosmo.models.Song;
import io.mokshjn.cosmo.utils.StorageUtils;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener,
        MediaPlayer.OnBufferingUpdateListener, AudioManager.OnAudioFocusChangeListener {

    public static final String ACTION_PLAY = "io.moshjn.cosmo.ACTION_PLAY";
    public static final String ACTION_PAUSE = "io.moshjn.cosmo.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "io.moshjn.cosmo.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "io.moshjn.cosmo.ACTION_NEXT";
    public static final String ACTION_STOP = "io.moshjn.cosmo.ACTION_STOP";

    final public static Uri sArtworkUri = Uri
            .parse("content://media/external/audio/albumart");

    private MediaPlayer player;

    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;

    private boolean ongoingCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;

    private static final int NOTIFICATION_ID = 659;

    private int resumeSongPosition;

    private AudioManager audioManager;


    private final IBinder musicBind = new MusicBinder();

    private ArrayList<Song> songList;
    private int songPosition = -1;
    private Song currentSong;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.reset();
        player.release();
        mediaSession.release();
        removeNotification();
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        callStateListener();
        registerBecomingNoisyReciever();
        register_playNewAudio();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            StorageUtils storage = new StorageUtils(getApplicationContext());
            songList = storage.loadSongs();

            songPosition = storage.loadSongPosition();

            if (songPosition != -1 && songPosition < songList.size()) {
                currentSong = songList.get(songPosition);
            } else {
                stopSelf();
            }
        } catch (NullPointerException e) {
            stopSelf();
        }

        if(!requestAudioFocus()) {
            stopSelf();
        }

        if(mediaSession == null) {
            try {
                initMediaSession();
                initMediaPlayer();
            } catch (RemoteException e) {
                e.printStackTrace();
                stopSelf();
            }
            buildNotification(PlaybackStatus.PLAYING);
        }

        handleIncomingActions(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(player != null) {
            player.release();
        }

        removeAudioFocus();

        if(phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        removeNotification();

        unregisterReceiver(becomingNoisyReceiver);
        unregisterReceiver(playNewAudio);

        new StorageUtils(getApplicationContext()).clearCachedAudioPlaylist();
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        playSong();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        skipToNext();
        updateMetaData();
        buildNotification(PlaybackStatus.PLAYING);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onAudioFocusChange(int i) {
        switch (i) {
            case AudioManager.AUDIOFOCUS_GAIN:
                initMediaPlayer();
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                if(player.isPlaying()){
                    player.stop();
                }
                player.reset();
                player.release();
                break;
            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                if(player.isPlaying()) {
                    player.pause();
                }
                break;
            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                if (player.isPlaying()) {
                    player.setVolume(0.1f, 0.1f);
                }
                break;
        }
    }

    private boolean requestAudioFocus() {
        audioManager= (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return true;
        }
        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioManager.abandonAudioFocus(this);
    }

    private void initMediaPlayer() {
        player = new MediaPlayer();

        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setOnPreparedListener(this);
        player.setOnErrorListener(this);
        player.setOnCompletionListener(this);
        player.setOnSeekCompleteListener(this);
        player.setOnInfoListener(this);
        player.setOnBufferingUpdateListener(this);

        player.reset();

        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            player.setDataSource(getApplicationContext(), ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSong.getId()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.prepareAsync();
    }

    public void playSong() {
        if(!player.isPlaying()){
            player.start();
        }
    }

    public void stopSong() {
        if(player == null) return;
        if(player.isPlaying()) {
            player.stop();
        }
    }

    public void pauseSong() {
        if(player.isPlaying()) {
            player.pause();
            resumeSongPosition = player.getCurrentPosition();
        }
    }

    public void resumeSong() {
        if(!player.isPlaying()) {
            player.seekTo(resumeSongPosition);
            player.start();
        }
    }

    private void skipToNext() {
        if(songPosition == songList.size() -1 ){
            songPosition = 0;
            currentSong = songList.get(songPosition);
        } else {
            currentSong = songList.get(++songPosition);
        }

        new StorageUtils(getApplicationContext()).storeAudioIndex(songPosition);

        stopSong();
        player.reset();
        initMediaPlayer();
    }

    private void skipToPrevious() {
        if(songPosition == 0) {
            songPosition = songList.size() -1;
            currentSong = songList.get(songPosition);
        } else {
            currentSong = songList.get(--songPosition);
        }

        new StorageUtils(getApplicationContext()).storeAudioIndex(songPosition);

        stopSong();
        player.reset();
        initMediaPlayer();
    }

    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            pauseSong();
            buildNotification(PlaybackStatus.PAUSED);
        }
    };

    private void registerBecomingNoisyReciever() {
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReceiver, intentFilter);
    }

    private void callStateListener() {
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        if(player != null) {
                            pauseSong();
                            ongoingCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        if(player != null) {
                            ongoingCall = false;
                            resumeSong();
                        }
                }
            }
        };

        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void initMediaSession() throws RemoteException {
        if (mediaSession != null) return;

        mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        mediaSession = new MediaSessionCompat(getApplicationContext(), "Cosmo");
        transportControls = mediaSession.getController().getTransportControls();
        mediaSession.setActive(true);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        updateMetaData();

        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();

                resumeSong();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onPause() {
                super.onPause();

                pauseSong();
                buildNotification(PlaybackStatus.PAUSED);
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();

                skipToNext();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();

                skipToPrevious();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onStop() {
                super.onStop();

                removeNotification();
                stopSelf();
            }

            @Override
            public void onSeekTo(long pos) {
                super.onSeekTo(pos);
            }
        });
    }

    private void updateMetaData() {
        Bitmap albumArt;
        if(currentSong == null) {
            albumArt = BitmapFactory.decodeResource(getResources(), R.drawable.nav_bg);
        } else {
            Uri uri = ContentUris.withAppendedId(sArtworkUri,
                    currentSong.getAlbumId());
            try {
                albumArt = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
                albumArt = BitmapFactory.decodeResource(getResources(), R.drawable.nav_bg);
            }
        }
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
        .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART , albumArt)
        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, currentSong.getArtist())
        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, currentSong.getAlbum())
        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, currentSong.getTitle())
        .build());
    }

    private void buildNotification(PlaybackStatus playbackStatus) {

        int notificationAction = android.R.drawable.ic_media_pause;
        PendingIntent play_pauseAction = null;

        if(playbackStatus == PlaybackStatus.PLAYING) {
            notificationAction = android.R.drawable.ic_media_pause;
            play_pauseAction = playbackAction(1);
        } else {
            notificationAction = android.R.drawable.ic_media_play;
            play_pauseAction = playbackAction(0);
        }

        Bitmap largeIcon;

        try {
            largeIcon = MediaStore.Images.Media.getBitmap(getContentResolver(), ContentUris.withAppendedId(sArtworkUri, currentSong.getAlbumId()));
        } catch (IOException e) {
            e.printStackTrace();
            largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.nav_bg);
        }

        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                .setShowWhen(false)
                .setStyle(new NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.getSessionToken())
                    .setShowActionsInCompactView(0, 1, 2))
                .setColor(getResources().getColor(R.color.colorAccent))
                .setLargeIcon(largeIcon)
                .setSmallIcon(android.R.drawable.stat_sys_headset)
                .setContentText(currentSong.getArtist())
                .setContentTitle(currentSong.getTitle())
                .setContentInfo(currentSong.getAlbum())
                .addAction(android.R.drawable.ic_media_previous, "previous", playbackAction(3))
                .addAction(notificationAction, "pause", play_pauseAction)
                .addAction(android.R.drawable.ic_media_next, "next", playbackAction(2));

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, builder.build());
    }

    private PendingIntent playbackAction(int actionNumber) {
        Intent playbackAction = new Intent(this, MusicService.class);
        switch (actionNumber) {
            case 0:
                playbackAction.setAction(ACTION_PLAY);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 1:
                playbackAction.setAction(ACTION_PAUSE);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 2:
                playbackAction.setAction(ACTION_NEXT);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 3:
                playbackAction.setAction(ACTION_PREVIOUS);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            default:
                break;
        }
        return null;
    }

    private void removeNotification(){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private void handleIncomingActions(Intent playbackAction) {
        if (playbackAction == null || playbackAction.getAction() == null) return;

        String actionString = playbackAction.getAction();
        if (actionString.equalsIgnoreCase(ACTION_PLAY)) {
            transportControls.play();
        } else if (actionString.equalsIgnoreCase(ACTION_PAUSE)) {
            transportControls.pause();
        } else if (actionString.equalsIgnoreCase(ACTION_NEXT)) {
            transportControls.skipToNext();
        } else if (actionString.equalsIgnoreCase(ACTION_PREVIOUS)) {
            transportControls.skipToPrevious();
        } else if (actionString.equalsIgnoreCase(ACTION_STOP)) {
            transportControls.stop();
        }
    }

    private BroadcastReceiver playNewAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            songPosition = new StorageUtils(getApplicationContext()).loadSongPosition();
            Log.d("songNo", "onReceive: "+songPosition);
            if(songPosition != -1 && songPosition < songList.size()){
                currentSong = songList.get(songPosition);
            } else {
                stopSelf();
            }

            stopSong();
            player.reset();
            initMediaPlayer();
            updateMetaData();
            buildNotification(PlaybackStatus.PLAYING);
        }
    };

    private void register_playNewAudio() {
        IntentFilter intentFilter = new IntentFilter(SongListFragment.Broadcast_PLAY_NEW_AUDIO);
        registerReceiver(playNewAudio, intentFilter);
    }


    public enum PlaybackStatus{
        PLAYING,
        PAUSED
    }
}
