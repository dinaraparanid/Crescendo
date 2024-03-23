package com.paranid5.crescendo.system.common.intent

import android.content.Context

val Context.mainActivityIntent
    get() = packageManager.getLaunchIntentForPackage("com.paranid5.crescendo")