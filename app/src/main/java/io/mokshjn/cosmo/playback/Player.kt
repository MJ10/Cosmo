package io.mokshjn.cosmo.playback

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.PowerManager
import android.util.Log
import android.widget.Toast

/**
 * Created by moksh on 27/11/17.
 */
abstract class Player : Playback, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    companion object {
        val TAG = javaClass.simpleName
    }

    var mCurrentMediaPlayer: MediaPlayer = MediaPlayer()
    var mNextMediaPlayer: MediaPlayer? = null

    lateinit var context: Context

    var playbackCallback: Playback.PlaybackCallbacks? = null

    var mIsInitialized = false

    constructor(context: Context) {
        this.context = context
        mCurrentMediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
    }

    override fun setDataSource(path: String): Boolean {
        mIsInitialized = false
        mIsInitialized = setDataSourceImpl(mCurrentMediaPlayer, path)
        if (mIsInitialized) setNextDataSource(null)
        return mIsInitialized
    }

    private fun setDataSourceImpl(player: MediaPlayer, path: String): Boolean {
        try {
            with(player) {
                reset()
                setOnPreparedListener { }
                if (path.startsWith("content://"))
                    setDataSource(context, Uri.parse(path))
                else
                    setDataSource(path)
                setAudioStreamType(AudioManager.STREAM_MUSIC)
                prepare()
            }
        } catch (e: Exception) {
            return false
        }
        player.setOnCompletionListener(this)
        player.setOnErrorListener(this)
        val intent = Intent(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION)
        with(intent) {
            putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId())
            putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.packageName)
            putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
            context.sendBroadcast(intent)
        }
        return true
    }

    override fun setNextDataSource(path: String?) {
        try {
            mCurrentMediaPlayer.setNextMediaPlayer(null)
        } catch (e: IllegalArgumentException) {
            Log.i(TAG, "Next MediaPlayer is current one, continuing")
        } catch (e: IllegalStateException) {
            Log.e(TAG, "MediaPlayer not initialized")
        }
        mNextMediaPlayer?.release()
        mNextMediaPlayer = null
        if (path == null) return

    }

    override fun setCallbacks(callbacks: Playback.PlaybackCallbacks) {
        playbackCallback = callbacks
    }

    override fun isInitialized() = mIsInitialized

    override fun start() = try {
        mCurrentMediaPlayer.start()
        true
    } catch (e: IllegalStateException) {
        false
    }

    override fun stop() {
        mCurrentMediaPlayer.stop()
        mIsInitialized = false
    }

    override fun release() {
        stop()
        mCurrentMediaPlayer.release()
        mNextMediaPlayer?.release()
    }

    override fun pause() = try {
        mCurrentMediaPlayer.pause()
        true
    } catch (e: IllegalStateException) {
        false
    }

    override fun isPlaying() = mIsInitialized and mCurrentMediaPlayer.isPlaying

    override fun duration(): Int {
        if (!mIsInitialized) return -1
        return try {
            mCurrentMediaPlayer.duration
        } catch (e: IllegalStateException) {
            -1
        }
    }

    override fun position(): Int {
        if (!mIsInitialized) return -1
        return try {
            mCurrentMediaPlayer.currentPosition
        } catch (e: IllegalStateException) {
            -1
        }
    }

    override fun seek(to: Int) = try {
        mCurrentMediaPlayer.seekTo(to)
        to
    } catch (e: IllegalStateException) {
        -1
    }

    override fun setVolume(volume: Float) = try {
        mCurrentMediaPlayer.setVolume(volume, volume)
        true
    } catch (e: IllegalStateException) {
        false
    }

    override fun setAudioSessionId(sessionId: Int) = try {
        mCurrentMediaPlayer.audioSessionId = sessionId
        true
    } catch (e: IllegalArgumentException) {
        false
    } catch (e: IllegalStateException) {
        false
    }

    override fun getAudioSessionId() = mCurrentMediaPlayer.audioSessionId

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        mIsInitialized = false
        mCurrentMediaPlayer.release()
        mCurrentMediaPlayer = MediaPlayer()
        mCurrentMediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
        Toast.makeText(context, "Couldn't play this song", Toast.LENGTH_LONG).show()
        return false
    }

    override fun onCompletion(mp: MediaPlayer?) {
        if (mp == mCurrentMediaPlayer && mNextMediaPlayer != null) {
            mIsInitialized = false
            mCurrentMediaPlayer.release()
            mCurrentMediaPlayer = mNextMediaPlayer!!
            mIsInitialized = true
            mNextMediaPlayer = null
            playbackCallback?.onTrackWentToNext()
        } else {
            playbackCallback?.onTrackEnded()
        }
    }

}