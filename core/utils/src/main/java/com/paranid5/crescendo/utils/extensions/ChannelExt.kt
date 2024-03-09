package com.paranid5.crescendo.utils.extensions

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration

suspend inline fun <T> Channel<T>.receiveTimeout(duration: Duration) = runCatching {
    withTimeout(duration) { receive() }
}

suspend inline fun <T> Channel<T>.receiveTimeout(millis: Long) = runCatching {
    withTimeout(millis) { receive() }
}