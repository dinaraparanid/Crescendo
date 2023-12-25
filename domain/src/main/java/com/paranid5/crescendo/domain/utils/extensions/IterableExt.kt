package com.paranid5.crescendo.domain.utils.extensions

inline fun <T> Iterable<T>.forEachStepped(step: Int, action: (T) -> Unit) =
    forEachIndexed { i, item -> if (i % step == 0) action(item) }

inline fun <T> Iterable<T>.forEachIndexedStepped(step: Int, action: (Int, T) -> Unit) =
    forEachIndexed { i, item -> if (i % step == 0) action(i, item) }