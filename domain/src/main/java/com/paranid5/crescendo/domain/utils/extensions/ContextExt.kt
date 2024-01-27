package com.paranid5.crescendo.domain.utils.extensions

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager

@SuppressLint("UnspecifiedRegisterReceiverFlag")
fun Context.registerReceiverCompat(
    receiver: BroadcastReceiver,
    filter: IntentFilter,
) = LocalBroadcastManager
    .getInstance(this)
    .registerReceiver(receiver, filter)

fun Context.registerReceiverCompat(
    receiver: BroadcastReceiver,
    vararg actions: String,
) = registerReceiverCompat(
    receiver = receiver,
    filter = IntentFilter().also { actions.forEach(it::addAction) }
)

fun Context.sendBroadcast(action: String) =
    LocalBroadcastManager
        .getInstance(this)
        .sendBroadcast(Intent(action))

fun Context.sendBroadcastCompat(intent: Intent) =
    LocalBroadcastManager
        .getInstance(this)
        .sendBroadcast(intent)