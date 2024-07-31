package com.paranid5.crescendo.system.common.intent

import android.content.Context
import android.content.Intent

private const val MAIN_ACTIVITY_LOCATION = "com.paranid5.crescendo.presentation.main.MainActivity"

val Context.mainActivityIntent
    get() = Intent(this, Class.forName(MAIN_ACTIVITY_LOCATION)).setFlags(
        Intent.FLAG_ACTIVITY_CLEAR_TASK
                or Intent.FLAG_ACTIVITY_NEW_TASK
                or Intent.FLAG_ACTIVITY_TASK_ON_HOME
    )

fun Context.openMainActivity() = startActivity(mainActivityIntent)