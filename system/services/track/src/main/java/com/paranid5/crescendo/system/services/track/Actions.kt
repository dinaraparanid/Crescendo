package com.paranid5.crescendo.system.services.track

import com.paranid5.crescendo.system.common.broadcast.TrackServiceBroadcasts
import com.paranid5.system.services.common.ServiceAction

internal sealed class Actions(
    override val requestCode: Int,
    override val playbackAction: String
) : ServiceAction {
    data object Pause : Actions(
        requestCode = 1,
        playbackAction = TrackServiceBroadcasts.Broadcast_PAUSE
    )

    data object Resume : Actions(
        requestCode = 2,
        playbackAction = TrackServiceBroadcasts.Broadcast_RESUME
    )

    data object PrevTrack : Actions(
        requestCode = 3,
        playbackAction = TrackServiceBroadcasts.Broadcast_PREV_TRACK
    )

    data object NextTrack : Actions(
        requestCode = 4,
        playbackAction = TrackServiceBroadcasts.Broadcast_NEXT_TRACK
    )

    data object Repeat : Actions(
        requestCode = 7,
        playbackAction = TrackServiceBroadcasts.Broadcast_REPEAT_CHANGED
    )

    data object Unrepeat : Actions(
        requestCode = 8,
        playbackAction = TrackServiceBroadcasts.Broadcast_REPEAT_CHANGED
    )

    data object Dismiss : Actions(
        requestCode = 9,
        playbackAction = TrackServiceBroadcasts.Broadcast_DISMISS_NOTIFICATION
    )
}