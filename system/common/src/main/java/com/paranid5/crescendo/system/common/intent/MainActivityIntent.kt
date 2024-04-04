package com.paranid5.crescendo.system.common.intent

import android.content.Context
import android.content.Intent

private const val MAIN_ACTIVITY_LOCATION = "com.paranid5.crescendo.presentation.main.MainActivity"

val Context.mainActivityIntent
    get() = Intent(this, Class.forName(MAIN_ACTIVITY_LOCATION))

fun Context.openMainActivity() = startActivity(mainActivityIntent)