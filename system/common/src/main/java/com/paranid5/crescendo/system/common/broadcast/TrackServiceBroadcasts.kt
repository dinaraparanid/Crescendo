package com.paranid5.crescendo.system.common.broadcast

data object TrackServiceBroadcasts {
    private const val SERVICE_LOCATION = "com.paranid5.crescendo.services.track_service"

    const val Broadcast_PAUSE = "$SERVICE_LOCATION.PAUSE"
    const val Broadcast_RESUME = "$SERVICE_LOCATION.RESUME"
    const val Broadcast_SWITCH_PLAYLIST = "$SERVICE_LOCATION.SWITCH_PLAYLIST"

    const val Broadcast_ADD_TRACK = "$SERVICE_LOCATION.ADD_TRACK"
    const val Broadcast_REMOVE_TRACK = "$SERVICE_LOCATION.REMOVE_TRACK"
    const val Broadcast_PLAYLIST_DRAGGED = "$SERVICE_LOCATION.PLAYLIST_DRAGGED"

    const val Broadcast_PREV_TRACK = "$SERVICE_LOCATION.PREV_TRACK"
    const val Broadcast_NEXT_TRACK = "$SERVICE_LOCATION.NEXT_TRACK"
    const val Broadcast_SEEK_TO = "$SERVICE_LOCATION.SEEK_TO"

    const val Broadcast_REPEAT_CHANGED = "$SERVICE_LOCATION.REPEAT_CHANGED"
    const val Broadcast_DISMISS_NOTIFICATION = "$SERVICE_LOCATION.DISMISS_NOTIFICATION"
    const val Broadcast_STOP = "$SERVICE_LOCATION.STOP"

    const val START_TYPE_ARG = "start_type"
    const val TRACK_ARG = "track"
    const val TRACK_INDEX_ARG = "track_index"
    const val POSITION_ARG = "position"
}