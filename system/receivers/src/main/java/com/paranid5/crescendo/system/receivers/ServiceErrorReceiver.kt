package com.paranid5.crescendo.system.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class ServiceErrorReceiver : BroadcastReceiver() {
    companion object {
        private const val RECEIVER_LOCATION = "com.paranid5.crescendo.system.receivers"
        const val Broadcast_SERVICE_ERROR = "$RECEIVER_LOCATION.SERVICE_ERROR"
        const val ERROR_MESSAGE_ARG = "message_error"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val error = intent.getStringExtra(ERROR_MESSAGE_ARG)!!
        Toast.makeText(context.applicationContext, error, Toast.LENGTH_LONG).show()
    }
}