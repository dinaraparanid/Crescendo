package com.paranid5.crescendo.system.common.broadcast

data object StreamServiceBroadcasts {
    private const val SERVICE_LOCATION = "com.paranid5.crescendo.services.stream_service"

    const val Broadcast_PAUSE = "$SERVICE_LOCATION.PAUSE"
    const val Broadcast_RESUME = "$SERVICE_LOCATION.RESUME"
    const val Broadcast_SWITCH_VIDEO = "$SERVICE_LOCATION.SWITCH_VIDEO"

    const val Broadcast_10_SECS_BACK = "$SERVICE_LOCATION.10_SECS_BACK"
    const val Broadcast_10_SECS_FORWARD = "$SERVICE_LOCATION.10_SECS_FORWARD"
    const val Broadcast_SEEK_TO = "$SERVICE_LOCATION.SEEK_TO"

    const val Broadcast_REPEAT_CHANGED = "$SERVICE_LOCATION.REPEAT_CHANGED"
    const val Broadcast_DISMISS_NOTIFICATION = "$SERVICE_LOCATION.DISMISS_NOTIFICATION"
    const val Broadcast_STOP = "$SERVICE_LOCATION.STOP"

    const val URL_ARG = "url"
    const val POSITION_ARG = "position"
}