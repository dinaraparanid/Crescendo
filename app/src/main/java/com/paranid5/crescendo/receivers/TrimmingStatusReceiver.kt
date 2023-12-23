package com.paranid5.crescendo.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class TrimmingStatusReceiver : BroadcastReceiver() {
    companion object {
        private const val RECEIVER_LOCATION = "com.paranid5.crescendo.receivers"
        const val Broadcast_TRIMMING_COMPLETED = "$RECEIVER_LOCATION.TRIMMING_COMPLETED"
        const val TRIMMING_STATUS_ARG = "trimming_status_arg"
    }

    override fun onReceive(context: Context, intent: Intent) {
        println("Trimming received")
        val status = intent.getStringExtra(TRIMMING_STATUS_ARG)!!
        println(status)
        Toast.makeText(context.applicationContext, status, Toast.LENGTH_LONG).show()
    }
}