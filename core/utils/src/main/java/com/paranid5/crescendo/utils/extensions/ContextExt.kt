package com.paranid5.crescendo.utils.extensions

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes

tailrec fun Context.getActivity(): ComponentActivity? =
    when (this) {
        is ComponentActivity -> this
        is ContextWrapper -> baseContext.getActivity()
        else -> null
    }

fun Context.openAppSettings() = startActivity(
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    )
)

@SuppressLint("UnspecifiedRegisterReceiverFlag")
fun Context.registerReceiverCompat(
    receiver: BroadcastReceiver,
    filter: IntentFilter,
) = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
        registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)

    else -> registerReceiver(receiver, filter)
}

fun Context.registerReceiverCompat(
    receiver: BroadcastReceiver,
    vararg actions: String,
) = registerReceiverCompat(
    receiver = receiver,
    filter = IntentFilter().also { actions.forEach(it::addAction) }
)

fun Context.sendBroadcast(action: String) =
    sendBroadcast(Intent(action))

@ColorInt
@Suppress("DEPRECATION")
fun Context.getColorCompat(@ColorRes id: Int) = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> getColor(id)
    else -> resources.getColor(id)
}