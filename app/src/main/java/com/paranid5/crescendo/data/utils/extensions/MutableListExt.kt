package com.paranid5.crescendo.data.utils.extensions

fun <T> MutableList<T>.move(fromIdx: Int, toIdx: Int) = when {
    toIdx > fromIdx -> (fromIdx until toIdx).forEach { i ->
        this[i] = this[i + 1].also { this[i + 1] = this[i] }
    }

    else -> (fromIdx downTo toIdx + 1).forEach { i ->
        this[i] = this[i - 1].also { this[i - 1] = this[i] }
    }
}