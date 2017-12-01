package io.mokshjn.cosmo.playback

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi

/**
 * Created by moksh on 1/12/17.
 */
abstract class PlayingNotification {
    companion object {
        val NOTIFICATION_CHANNEL_ID = "playing_notification"
        val NOTIFICATION_ID = 1
        val NOTIFICATION_MODE_FOREGROUND = 1
        val NOTIFICATION_MODE_BACKGROUND = 0
    }

    protected lateinit var musicService: MusicService
    var stopped: Boolean = false
    private var notifyMode = NOTIFICATION_MODE_BACKGROUND
    private lateinit var notificationManager: NotificationManager

    fun init(service: MusicService) {
        synchronized(this) {
            this.musicService = service
            this.notificationManager = service.getSystemService(
                    Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                createNotificationChannel()
        }
    }

    abstract fun update()

    fun stop() {
        synchronized(this) {
            stopped = true
            musicService.stopForeground(true)
            notificationManager.cancel(NOTIFICATION_ID)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        var channel = notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID)
        if (channel == null) {
        }
    }
}