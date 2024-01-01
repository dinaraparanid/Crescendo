package com.paranid5.crescendo.services.stream_service

import com.paranid5.crescendo.services.ServiceAction

sealed class Actions(
    override val requestCode: Int,
    override val playbackAction: String
) : ServiceAction {
    data object Pause : Actions(
        requestCode = 1,
        playbackAction = StreamService.Broadcast_PAUSE
    )

    data object Resume : Actions(
        requestCode = 2,
        playbackAction = StreamService.Broadcast_RESUME
    )

    data object TenSecsBack : Actions(
        requestCode = 3,
        playbackAction = StreamService.Broadcast_10_SECS_BACK
    )

    data object TenSecsForward : Actions(
        requestCode = 4,
        playbackAction = StreamService.Broadcast_10_SECS_FORWARD
    )

    data object Repeat : Actions(
        requestCode = 7,
        playbackAction = StreamService.Broadcast_REPEAT_CHANGED
    )

    data object Unrepeat : Actions(
        requestCode = 8,
        playbackAction = StreamService.Broadcast_REPEAT_CHANGED
    )

    data object Dismiss : Actions(
        requestCode = 9,
        playbackAction = StreamService.Broadcast_DISMISS_NOTIFICATION
    )
}