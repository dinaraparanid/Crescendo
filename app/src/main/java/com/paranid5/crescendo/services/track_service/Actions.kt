package com.paranid5.crescendo.services.track_service

import com.paranid5.crescendo.services.ServiceAction

sealed class Actions(
    override val requestCode: Int,
    override val playbackAction: String
) : ServiceAction {
    data object Pause : Actions(
        requestCode = TrackService.NOTIFICATION_ID + 1,
        playbackAction = TrackService.Broadcast_PAUSE
    )

    data object Resume : Actions(
        requestCode = TrackService.NOTIFICATION_ID + 2,
        playbackAction = TrackService.Broadcast_RESUME
    )

    data object PrevTrack : Actions(
        requestCode = TrackService.NOTIFICATION_ID + 3,
        playbackAction = TrackService.Broadcast_PREV_TRACK
    )

    data object NextTrack : Actions(
        requestCode = TrackService.NOTIFICATION_ID + 4,
        playbackAction = TrackService.Broadcast_NEXT_TRACK
    )

    data object Repeat : Actions(
        requestCode = TrackService.NOTIFICATION_ID + 7,
        playbackAction = TrackService.Broadcast_CHANGE_REPEAT
    )

    data object Unrepeat : Actions(
        requestCode = TrackService.NOTIFICATION_ID + 8,
        playbackAction = TrackService.Broadcast_CHANGE_REPEAT
    )

    data object Dismiss : Actions(
        requestCode = TrackService.NOTIFICATION_ID + 9,
        playbackAction = TrackService.Broadcast_DISMISS_NOTIFICATION
    )
}