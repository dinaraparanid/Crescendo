package com.paranid5.crescendo.domain.utils.extensions

import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

fun <T> MutableList<T>.move(fromIdx: Int, toIdx: Int) = when {
    toIdx > fromIdx -> (fromIdx until toIdx).forEach { i ->
        this[i] = this[i + 1].also { this[i + 1] = this[i] }
    }

    else -> (fromIdx downTo toIdx + 1).forEach { i ->
        this[i] = this[i - 1].also { this[i - 1] = this[i] }
    }
}

fun <T> List<T>.moved(fromIdx: Int, toIdx: Int) = when {
    toIdx == fromIdx -> toImmutableList()
    toIdx > fromIdx -> movedForward(fromIdx, toIdx)
    else -> movedBackward(fromIdx, toIdx)
}

private fun <T> List<T>.movedForward(fromIdx: Int, toIdx: Int) =
    persistentListOf<T>().mutate {
        it.addAll(take(fromIdx))
        it.addAll(subList(fromIdx + 1, toIdx + 1))
        it.add(get(fromIdx))
        it.addAll(drop(toIdx + 1))
    }

private fun <T> List<T>.movedBackward(fromIdx: Int, toIdx: Int) =
    persistentListOf<T>().mutate {
        it.addAll(take(fromIdx))
        it.add(get(fromIdx))
        it.addAll(subList(toIdx, fromIdx))
        it.addAll(drop(fromIdx + 1))
    }