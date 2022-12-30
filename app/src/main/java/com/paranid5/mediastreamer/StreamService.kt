package com.paranid5.mediastreamer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Build
import androidx.annotation.RequiresApi

class StreamService : Service() {
    companion object {
        private const val STREAM_CHANNEL_ID = "stream_channel"

        const val Broadcast_PAUSE = "com.paranid5.mediastreamer.StreamService.PAUSE"
        const val Broadcast_RESUME = "com.paranid5.mediastreamer.StreamService.RESUME"
        const val Broadcast_SWITCH = "com.paranid5.mediastreamer.StreamService.SWITCH"

        const val URL_ARG = "url"

        const val ACTION_PAUSE = "pause"
        const val ACTION_RESUME = "resume"
        const val ACTION_DISMISS = "dismiss"
    }

    private val binder = object : Binder() {}
    private var url = ""

    private val pauseReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            pausePlayback()
        }
    }

    private val resumeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            resumePlayback()
        }
    }

    private val switchReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            switchToAnotherStream()
        }
    }

    override fun onCreate() {
        super.onCreate()
        registerReceiver(pauseReceiver, IntentFilter(Broadcast_PAUSE))
        registerReceiver(resumeReceiver, IntentFilter(Broadcast_RESUME))
        registerReceiver(switchReceiver, IntentFilter(Broadcast_SWITCH))
    }

    override fun onBind(intent: Intent?) = binder

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel()

        url = intent.getStringExtra(URL_ARG)!!

        // TODO: start playing

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(pauseReceiver)
        unregisterReceiver(resumeReceiver)
        unregisterReceiver(switchReceiver)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() =
        (getSystemService(NOTIFICATION_SERVICE)!! as NotificationManager)
            .createNotificationChannel(
                NotificationChannel(
                    STREAM_CHANNEL_ID,
                    "Stream",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    setShowBadge(true)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                    setSound(null, null)
                }
            )

    private fun pausePlayback() {
        // TODO: pause playback
    }

    private fun resumePlayback() {
        // TODO: resume playback
    }

    private fun switchToAnotherStream() {
        // TODO: switch to another stream / video
    }

    private fun handleIncomingActions(intent: Intent) {
        val actionTitle = intent.action

        when {
            actionTitle.equals(ACTION_PAUSE, ignoreCase = true) -> pausePlayback()
            actionTitle.equals(ACTION_RESUME, ignoreCase = true) -> resumePlayback()
            actionTitle.equals(ACTION_DISMISS, ignoreCase = true) -> removeNotification()
        }
    }

    private fun buildNotification() {
        // TODO: notification
    }

    private fun removeNotification() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> stopForeground(STOP_FOREGROUND_REMOVE)
        else -> stopForeground(true)
    }
}