package io.mokshjn.cosmo.playback

/**
 * Created by moksh on 27/11/17.
 */
interface Playback {
    fun setDataSource(path: String): Boolean

    fun setNextDataSource(path: String?)

    fun setCallbacks(callbacks: PlaybackCallbacks)

    fun isInitialized(): Boolean

    fun start(): Boolean

    fun stop()

    fun release()

    fun pause(): Boolean

    fun isPlaying(): Boolean

    fun duration(): Int

    fun position(): Int

    fun seek(to: Int): Int

    fun setVolume(volume: Float): Boolean

    fun setAudioSessionId(sessionId: Int): Boolean

    fun getAudioSessionId(): Int

    interface PlaybackCallbacks {
        fun onTrackWentToNext()
        fun onTrackEnded()
    }
}