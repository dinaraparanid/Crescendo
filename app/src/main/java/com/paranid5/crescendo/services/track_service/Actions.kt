package com.paranid5.crescendo.services.track_service

import com.paranid5.crescendo.services.ServiceAction
import com.paranid5.crescendo.services.track_service.notification.TRACKS_NOTIFICATION_ID

sealed class Actions(
    override val requestCode: Int,
    override val playbackAction: String
) : ServiceAction {
    data object Pause : Actions(
        requestCode = 1,
        playbackAction = TrackService.Broadcast_PAUSE
    )

    data object Resume : Actions(
        requestCode = 2,
        playbackAction = TrackService.Broadcast_RESUME
    )

    data object PrevTrack : Actions(
        requestCode = 3,
        playbackAction = TrackService.Broadcast_PREV_TRACK
    )

    data object NextTrack : Actions(
        requestCode = 4,
        playbackAction = TrackService.Broadcast_NEXT_TRACK
    )

    data object Repeat : Actions(
        requestCode = 7,
        playbackAction = TrackService.Broadcast_REPEAT_CHANGED
    )

    data object Unrepeat : Actions(
        requestCode = 8,
        playbackAction = TrackService.Broadcast_REPEAT_CHANGED
    )

    data object Dismiss : Actions(
        requestCode = 9,
        playbackAction = TrackService.Broadcast_DISMISS_NOTIFICATION
    )
}