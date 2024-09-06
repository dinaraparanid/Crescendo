package com.paranid5.crescendo.utils.extensions

fun <K, V> MutableMap<K, V>.replaceWith(other: Map<K, V>) {
    clear()
    putAll(other)
}
