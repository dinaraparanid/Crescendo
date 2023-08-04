package com.paranid5.crescendo.domain.utils

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration

class AsyncCondVar {
    private val channel = Channel<Unit>(Channel.CONFLATED)

    internal suspend inline fun notify() = channel.send(Unit)

    internal suspend inline fun wait() = channel.receive()

    internal suspend inline fun wait(timeoutMs: Long) = kotlin.runCatching {
        withTimeout(timeoutMs) { channel.receive() }
    }

    internal suspend inline fun wait(timeout: Duration) =
        withTimeout(timeout) { channel.receive() }
}