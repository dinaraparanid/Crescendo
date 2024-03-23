package com.paranid5.crescendo.system.services.stream

import com.paranid5.crescendo.system.common.broadcast.StreamServiceBroadcasts
import com.paranid5.system.services.common.ServiceAction

sealed class Actions(
    override val requestCode: Int,
    override val playbackAction: String
) : ServiceAction {
    data object Pause : Actions(
        requestCode = 1,
        playbackAction = StreamServiceBroadcasts.Broadcast_PAUSE
    )

    data object Resume : Actions(
        requestCode = 2,
        playbackAction = StreamServiceBroadcasts.Broadcast_RESUME
    )

    data object TenSecsBack : Actions(
        requestCode = 3,
        playbackAction = StreamServiceBroadcasts.Broadcast_10_SECS_BACK
    )

    data object TenSecsForward : Actions(
        requestCode = 4,
        playbackAction = StreamServiceBroadcasts.Broadcast_10_SECS_FORWARD
    )

    data object Repeat : Actions(
        requestCode = 7,
        playbackAction = StreamServiceBroadcasts.Broadcast_REPEAT_CHANGED
    )

    data object Unrepeat : Actions(
        requestCode = 8,
        playbackAction = StreamServiceBroadcasts.Broadcast_REPEAT_CHANGED
    )

    data object Dismiss : Actions(
        requestCode = 9,
        playbackAction = StreamServiceBroadcasts.Broadcast_DISMISS_NOTIFICATION
    )
}