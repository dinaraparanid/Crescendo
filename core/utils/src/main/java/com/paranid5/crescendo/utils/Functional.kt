package com.paranid5.crescendo.utils

val doNothing = {}

fun <T> identity(x: T) = x

inline fun <T> Boolean.takeIfTrueOrNull(func: () -> T?) =
    if (this) func() else null
