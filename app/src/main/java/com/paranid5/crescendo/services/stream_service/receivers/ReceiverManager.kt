package com.paranid5.crescendo.services.stream_service.receivers

import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.paranid5.crescendo.domain.utils.extensions.registerReceiverCompat
import com.paranid5.crescendo.services.stream_service.StreamService

@Suppress("IncorrectFormatting")
internal fun StreamService.registerReceivers() {
    registerReceiverCompat(pauseReceiver, StreamService.Broadcast_PAUSE)
    registerReceiverCompat(resumeReceiver, StreamService.Broadcast_RESUME)
    registerReceiverCompat(switchVideoReceiver, StreamService.Broadcast_SWITCH_VIDEO)
    registerReceiverCompat(tenSecsBackReceiver, StreamService.Broadcast_10_SECS_BACK)
    registerReceiverCompat(tenSecsForwardReceiver, StreamService.Broadcast_10_SECS_FORWARD)
    registerReceiverCompat(seekToReceiver, StreamService.Broadcast_SEEK_TO)
    registerReceiverCompat(repeatChangedReceiver, StreamService.Broadcast_REPEAT_CHANGED)
    registerReceiverCompat(dismissNotificationReceiver, StreamService.Broadcast_DISMISS_NOTIFICATION)
    registerReceiverCompat(stopReceiver, StreamService.Broadcast_STOP)
}

internal fun StreamService.unregisterReceivers() =
    LocalBroadcastManager.getInstance(this).run {
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
