package com.paranid5.crescendo.utils.extensions

inline val <T> T?.notNull
    get() = requireNotNull(this)
