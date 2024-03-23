package com.paranid5.crescendo.system.services.video_cache

import com.paranid5.crescendo.system.common.broadcast.VideoCacheServiceBroadcasts
import com.paranid5.system.services.common.ServiceAction

internal sealed class Actions(
    override val requestCode: Int,
    override val playbackAction: String
) : ServiceAction {
    data object CancelCurVideo : Actions(
        requestCode = 1,
        playbackAction = VideoCacheServiceBroadcasts.Broadcast_CANCEL_CUR_VIDEO
    )

    data object CancelAll : Actions(
        requestCode = 2,
        playbackAction = VideoCacheServiceBroadcasts.Broadcast_CANCEL_ALL
    )
}