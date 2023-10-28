package com.paranid5.crescendo.domain.utils

import com.paranid5.crescendo.domain.utils.extensions.receiveTimeout
import kotlinx.coroutines.channels.Channel
import kotlin.time.Duration

class AsyncCondVar {
    private val channel = Channel<Unit>(Channel.CONFLATED)

    internal suspend inline fun notify() = channel.send(Unit)

    internal suspend inline fun wait() = channel.receive()

    internal suspend inline fun wait(timeoutMs: Long) =
        channel.receiveTimeout(timeoutMs)

    internal suspend inline fun wait(timeout: Duration) =
        channel.receiveTimeout(timeout)
}