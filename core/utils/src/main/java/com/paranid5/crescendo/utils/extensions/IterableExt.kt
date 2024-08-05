package com.paranid5.crescendo.utils.extensions

import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf

inline fun <T> Iterable<T>.forEachStepped(step: Int, action: (T) -> Unit) =
    forEachIndexed { i, item -> if (i % step == 0) action(item) }

inline fun <T> Iterable<T>.forEachIndexedStepped(step: Int, action: (Int, T) -> Unit) =
    forEachIndexed { i, item -> if (i % step == 0) action(i, item) }

inline fun <T, R> Iterable<T>.mapToImmutableList(transform: (T) -> R) =
    persistentListOf<R>().mutate { listBuilder ->
        forEach { listBuilder.add(transform(it)) }
    }

inline fun <T> Iterable<T>.filterToImmutableList(predicate: (T) -> Boolean) =
    persistentListOf<T>().mutate { listBuilder ->
        forEach { if (predicate(it)) listBuilder.add(it) }
    }

fun <T> Iterable<T>.exclude(index: Int) =
    persistentListOf<T>().mutate { listBuilder ->
        listBuilder.addAll(take(index))
        listBuilder.addAll(drop(index + 1))
    }

fun <T> Iterable<T>.replace(index: Int, elem: T) =
    buildList {
        addAll(this@replace.take(index))
        add(elem)
        addAll(drop(index + 1))
    }

