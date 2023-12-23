package com.paranid5.crescendo.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class StreamingErrorReceiver : BroadcastReceiver() {
    companion object {
        private const val RECEIVER_LOCATION = "com.paranid5.crescendo.receivers"
        const val Broadcast_STREAMING_ERROR = "$RECEIVER_LOCATION.STREAMING_ERROR"
        const val STREAMING_ERROR_ARG = "streaming_error"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val error = intent.getStringExtra(STREAMING_ERROR_ARG)!!
        Toast.makeText(context.applicationContext, error, Toast.LENGTH_LONG).show()
    }
}