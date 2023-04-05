package com.paranid5.mediastreamer.presentation.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext

@Composable
fun BroadcastReceiver(action: String, onReceive: (context: Context?, intent: Intent?) -> Unit) {
    val context = LocalContext.current
    val currentOnReceive by rememberUpdatedState(newValue = onReceive)

    DisposableEffect(context, action) {
        val intentFilter = IntentFilter(action)

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) =
                currentOnReceive(context, intent)
        }

        context.registerReceiver(receiver, intentFilter)
        onDispose { context.unregisterReceiver(receiver) }
    }
}