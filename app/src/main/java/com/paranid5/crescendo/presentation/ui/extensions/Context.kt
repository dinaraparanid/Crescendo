package com.paranid5.crescendo.presentation.ui.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.paranid5.crescendo.presentation.main.MainActivity

fun Context.openMainActivity() =
    startActivity(Intent(this, MainActivity::class.java))

fun Context.openAppSettings() = startActivity(
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    )
)