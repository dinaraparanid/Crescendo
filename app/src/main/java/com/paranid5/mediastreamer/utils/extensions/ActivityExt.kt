package com.paranid5.mediastreamer.utils.extensions

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings

fun Activity.openAppSettings() = startActivity(
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    )
)