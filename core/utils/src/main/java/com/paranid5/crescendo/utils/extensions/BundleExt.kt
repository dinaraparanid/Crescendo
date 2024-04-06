package com.paranid5.crescendo.utils.extensions

import android.os.Build
import android.os.Bundle

fun <T> Bundle.getParcelableCompat(key: String, clazz: Class<T>) = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelable(key, clazz)
    else -> getParcelable(key)
}