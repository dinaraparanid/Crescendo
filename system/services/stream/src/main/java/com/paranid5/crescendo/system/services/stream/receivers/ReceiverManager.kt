package com.paranid5.crescendo.system.services.stream.receivers

import com.paranid5.crescendo.system.common.broadcast.StreamServiceBroadcasts
import com.paranid5.crescendo.system.services.stream.StreamService
import com.paranid5.crescendo.utils.extensions.registerReceiverCompat

@Suppress("IncorrectFormatting")
internal fun StreamService.registerReceivers() {
    registerReceiverCompat(pauseReceiver, StreamServiceBroadcasts.Broadcast_PAUSE)
    registerReceiverCompat(resumeReceiver, StreamServiceBroadcasts.Broadcast_RESUME)
    registerReceiverCompat(switchVideoReceiver, StreamServiceBroadcasts.Broadcast_SWITCH_VIDEO)
    registerReceiverCompat(tenSecsBackReceiver, StreamServiceBroadcasts.Broadcast_10_SECS_BACK)
    registerReceiverCompat(tenSecsForwardReceiver, StreamServiceBroadcasts.Broadcast_10_SECS_FORWARD)
    registerReceiverCompat(seekToReceiver, StreamServiceBroadcasts.Broadcast_SEEK_TO)
    registerReceiverCompat(repeatChangedReceiver, StreamServiceBroadcasts.Broadcast_REPEAT_CHANGED)
    registerReceiverCompat(dismissNotificationReceiver, StreamServiceBroadcasts.Broadcast_DISMISS_NOTIFICATION)
    registerReceiverCompat(stopReceiver, StreamServiceBroadcasts.Broadcast_STOP)
}

internal fun StreamService.unregisterReceivers() {
    unregisterReceiver(pauseReceiver)
    unregisterReceiver(resumeReceiver)
    unregisterReceiver(switchVideoReceiver)
    unregisterReceiver(tenSecsBackReceiver)
    unregisterReceiver(tenSecsForwardReceiver)
    unregisterReceiver(seekToReceiver)
    unregisterReceiver(repeatChangedReceiver)
    unregisterReceiver(dismissNotificationReceiver)
    unregisterReceiver(stopReceiver)
}
