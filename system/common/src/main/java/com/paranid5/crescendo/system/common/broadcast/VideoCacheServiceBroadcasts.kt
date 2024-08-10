package com.paranid5.crescendo.system.common.broadcast

data object VideoCacheServiceBroadcasts {
    private const val SERVICE_LOCATION = "com.paranid5.crescendo.services.video_cache_service"
    const val Broadcast_CACHE_NEXT_VIDEO = "$SERVICE_LOCATION.CACHE_NEXT_VIDEO"
    const val Broadcast_CANCEL_CUR_VIDEO = "$SERVICE_LOCATION.CANCEL_CUR_VIDEO"
    const val Broadcast_CANCEL_ALL = "$SERVICE_LOCATION.CANCEL_ALL"

    const val URL_ARG = "url"
    const val FILENAME_ARG = "filename"
    const val FORMAT_ARG = "format"
    const val TRIM_RANGE_ARG = "trim_range"
}
