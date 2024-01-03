package com.paranid5.crescendo.services.video_cache_service

import com.paranid5.crescendo.services.ServiceAction

sealed class Actions(
    override val requestCode: Int,
    override val playbackAction: String
) : ServiceAction {
    data object CancelCurVideo : Actions(
        requestCode = 1,
        playbackAction = VideoCacheService.Broadcast_CANCEL_CUR_VIDEO
    )

    data object CancelAll : Actions(
        requestCode = 2,
        playbackAction = VideoCacheService.Broadcast_CANCEL_ALL
    )
}