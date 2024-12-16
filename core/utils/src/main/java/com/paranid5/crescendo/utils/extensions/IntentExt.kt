package com.paranid5.crescendo.utils.extensions

import android.content.Intent

fun <T> Intent.getParcelableCompat(key: String, clazz: Class<T>): T? =
    extras?.getParcelableCompat(key, clazz)