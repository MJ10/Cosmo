package io.mokshjn.cosmo.playback

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Message
import android.os.PowerManager
import android.util.Log
import android.view.KeyEvent
import io.mokshjn.cosmo.BuildConfig
import io.mokshjn.cosmo.constants.Constants
import io.mokshjn.cosmo.services.MusicService

/**
 * Created by moksh on 27/11/17.
 */
class MediaButtonIntentReceiver : BroadcastReceiver() {

    companion object {

        val TAG = MediaButtonIntentReceiver::class.java.simpleName
        private val DEBUG = BuildConfig.DEBUG
        private val MSG_HEADSET_DOUBLE_CLICK_TIMEOUT = 2
        private val DOUBLE_CLICK = 400

        private var mWakeLock: PowerManager.WakeLock? = null
        private var mClickCounter = 0
        private var mLastClickTime = 0L

        private var mHandler: Handler = Handler { msg ->
            when (msg.what) {
                MSG_HEADSET_DOUBLE_CLICK_TIMEOUT -> {
                    val clickCount = msg.arg1
                    val command: String?

                    if (DEBUG) Log.v(TAG, "Handling headset click, count = $clickCount")
                    command = when (clickCount) {
                        1 -> Constants.ACTION_TOGGLE_PAUSE
                        2 -> Constants.ACTION_SKIP
                        3 -> Constants.ACTION_REWIND
                        else -> null
                    }
                    if (command != null) {
                        val context = msg.obj as Context
                        startService(context, command)
                    }
                    true
                }
                else -> {
                    false
                }
            }
            releaseWakeLockIfHandlerIdle()
            true
        }

        fun handleIntent(context: Context, intent: Intent): Boolean {
            val intentAction = intent.action
            if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
                val keyEvent: KeyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT) ?: return false

                val keycode = keyEvent.keyCode
                val action = keyEvent.action
                val eventTime = keyEvent.eventTime

                val command: String?
                command = when (keycode) {
                    KeyEvent.KEYCODE_MEDIA_STOP -> Constants.ACTION_STOP
                    KeyEvent.KEYCODE_HEADSETHOOK, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> Constants.ACTION_TOGGLE_PAUSE
                    KeyEvent.KEYCODE_MEDIA_NEXT -> Constants.ACTION_SKIP
                    KeyEvent.KEYCODE_MEDIA_PREVIOUS -> Constants.ACTION_REWIND
                    KeyEvent.KEYCODE_MEDIA_PAUSE -> Constants.ACTION_PAUSE
                    KeyEvent.KEYCODE_MEDIA_PLAY -> Constants.ACTION_PLAY
                    else -> null
                }

                if (command != null) {
                    if (action == KeyEvent.ACTION_DOWN) {
                        if (eventTime - mLastClickTime >= DOUBLE_CLICK) {
                            mClickCounter = 0
                        }
                        mClickCounter++
                        if (DEBUG) Log.v(TAG, "Received headset click, count = $mClickCounter")
                        mHandler.removeMessages(MSG_HEADSET_DOUBLE_CLICK_TIMEOUT)

                        val msg = mHandler.obtainMessage(MSG_HEADSET_DOUBLE_CLICK_TIMEOUT,
                                mClickCounter, 0, context)
                        val delay = (if (mClickCounter < 3) DOUBLE_CLICK else 0).toLong()
                        if (mClickCounter >= 3) {
                            mClickCounter = 0
                        }
                        mLastClickTime = eventTime
                        acquireWakeLockAndSendMessage(context, msg, delay)
                    }
                } else {
                    startService(context, command)
                }
                return true
            }
            return false
        }

        private fun startService(context: Context, command: String?) {
            val intent = Intent(context, MusicService::class.java)
            intent.action = command
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                context.startForegroundService(intent)
            else
                context.startService(intent)
        }

        private fun acquireWakeLockAndSendMessage(context: Context, msg: Message, delay: Long) {
            if (mWakeLock == null) {
                val appContext = context.applicationContext
                val pm = appContext.getSystemService(Context.POWER_SERVICE) as PowerManager
                mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Cosmo Headset Button")
                mWakeLock!!.setReferenceCounted(false)
            }
            if (DEBUG) Log.v(TAG, "Acquiring wakelock and sending ${msg.what}")
            mWakeLock?.acquire(1000)
            mHandler.sendMessageDelayed(msg, delay)
        }

        private fun releaseWakeLockIfHandlerIdle() {
            if (mHandler.hasMessages(MSG_HEADSET_DOUBLE_CLICK_TIMEOUT)) {
                if (DEBUG) Log.v(TAG, "Still has messages, not releasing wakelock")
                return
            } else {
                if (DEBUG) Log.v(TAG, "Releasing wakelock")
                mWakeLock?.release()
                mWakeLock = null
            }
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (DEBUG) Log.v(TAG, "Received Intent: $intent")
        if (handleIntent(context!!, intent!!) && isOrderedBroadcast) abortBroadcast()
    }
}