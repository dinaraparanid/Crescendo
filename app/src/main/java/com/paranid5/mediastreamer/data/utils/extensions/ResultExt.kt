package com.paranid5.mediastreamer.data.utils.extensions

inline fun <T> Result<T>.expect(onError: (Throwable) -> Unit): Result<T> {
    if (isFailure) onError(exceptionOrNull()!!)
    return this
}