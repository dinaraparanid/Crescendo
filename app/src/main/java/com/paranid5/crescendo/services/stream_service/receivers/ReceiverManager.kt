package com.paranid5.crescendo.services.stream_service.receivers

import com.paranid5.crescendo.domain.utils.extensions.registerReceiverCompat
import com.paranid5.crescendo.services.stream_service.StreamService2

@Suppress("IncorrectFormatting")
fun StreamService2.registerReceivers() {
    registerReceiverCompat(pauseReceiver, StreamService2.Broadcast_PAUSE)
    registerReceiverCompat(resumeReceiver, StreamService2.Broadcast_RESUME)
    registerReceiverCompat(switchVideoReceiver, StreamService2.Broadcast_SWITCH_VIDEO)
    registerReceiverCompat(tenSecsBackReceiver, StreamService2.Broadcast_10_SECS_BACK)
    registerReceiverCompat(tenSecsForwardReceiver, StreamService2.Broadcast_10_SECS_FORWARD)
    registerReceiverCompat(seekToReceiver, StreamService2.Broadcast_SEEK_TO)
    registerReceiverCompat(repeatChangedReceiver, StreamService2.Broadcast_CHANGE_REPEAT)
    registerReceiverCompat(dismissNotificationReceiver, StreamService2.Broadcast_DISMISS_NOTIFICATION)
    registerReceiverCompat(stopReceiver, StreamService2.Broadcast_STOP)
}

fun StreamService2.unregisterReceivers() {
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
