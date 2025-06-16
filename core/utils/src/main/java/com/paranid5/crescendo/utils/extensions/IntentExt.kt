package com.paranid5.crescendo.utils.extensions

import android.content.Intent
import kotlin.reflect.KClass

fun <T : Any> Intent.getParcelableCompat(key: String, clazz: KClass<T>): T? =
    extras?.getParcelableCompat(key, clazz)
