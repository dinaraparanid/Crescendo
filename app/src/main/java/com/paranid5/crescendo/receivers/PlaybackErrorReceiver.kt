package com.paranid5.crescendo.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class PlaybackErrorReceiver : BroadcastReceiver() {
    companion object {
        private const val RECEIVER_LOCATION = "com.paranid5.crescendo.receivers"
        const val Broadcast_PLAYBACK_ERROR = "$RECEIVER_LOCATION.PLAYBACK_ERROR"
        const val ERROR_MESSAGE_ARG = "playback_error"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val error = intent.getStringExtra(ERROR_MESSAGE_ARG)!!
        Toast.makeText(context.applicationContext, error, Toast.LENGTH_LONG).show()
    }
}