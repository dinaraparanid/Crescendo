package com.paranid5.crescendo.utils.extensions

import android.os.Build
import android.os.Bundle
import kotlin.reflect.KClass

@Suppress("Deprecation")
fun <T : Any> Bundle.getParcelableCompat(key: String, clazz: KClass<T>) = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelable(key, clazz.java)
    else -> getParcelable(key)
}