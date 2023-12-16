package com.paranid5.crescendo.domain.utils.extensions

fun <T> MutableCollection<T>.replaceWith(elements: Iterable<T>) {
    clear()
    addAll(elements)
}

fun <T> MutableCollection<T>.replaceWith(elements: Sequence<T>) {
    clear()
    addAll(elements)
}

fun <T> MutableCollection<T>.replaceWith(elements: Collection<T>) {
    clear()
    addAll(elements)
}

fun <T> MutableCollection<T>.replaceWith(elements: Array<T>) {
    clear()
    addAll(elements)
}