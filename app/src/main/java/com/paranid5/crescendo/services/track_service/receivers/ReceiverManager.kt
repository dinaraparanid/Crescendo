package com.paranid5.crescendo.services.track_service.receivers

import com.paranid5.crescendo.utils.extensions.registerReceiverCompat
import com.paranid5.crescendo.services.track_service.TrackService

@Suppress("IncorrectFormatting")
internal fun TrackService.registerReceivers() {
    registerReceiverCompat(pauseReceiver, TrackService.Broadcast_PAUSE)
    registerReceiverCompat(resumeReceiver, TrackService.Broadcast_RESUME)
    registerReceiverCompat(switchPlaylistReceiver, TrackService.Broadcast_SWITCH_PLAYLIST)
    registerReceiverCompat(seekToReceiver, TrackService.Broadcast_SEEK_TO)
    registerReceiverCompat(seekToNextTrackReceiver, TrackService.Broadcast_NEXT_TRACK)
    registerReceiverCompat(seekToPrevTrackReceiver, TrackService.Broadcast_PREV_TRACK)
    registerReceiverCompat(repeatChangedReceiver, TrackService.Broadcast_REPEAT_CHANGED)
    registerReceiverCompat(addTrackReceiver, TrackService.Broadcast_ADD_TRACK)
    registerReceiverCompat(removeTrackReceiver, TrackService.Broadcast_REMOVE_TRACK)
    registerReceiverCompat(playlistDraggedReceiver, TrackService.Broadcast_PLAYLIST_DRAGGED)
    registerReceiverCompat(dismissNotificationReceiver, TrackService.Broadcast_DISMISS_NOTIFICATION)
    registerReceiverCompat(stopReceiver, TrackService.Broadcast_STOP)
}

internal fun TrackService.unregisterReceiver() {
    unregisterReceiver(pauseReceiver)
    unregisterReceiver(resumeReceiver)
    unregisterReceiver(switchPlaylistReceiver)
    unregisterReceiver(seekToReceiver)
    unregisterReceiver(seekToNextTrackReceiver)
    unregisterReceiver(seekToPrevTrackReceiver)
    unregisterReceiver(repeatChangedReceiver)
    unregisterReceiver(addTrackReceiver)
    unregisterReceiver(removeTrackReceiver)
    unregisterReceiver(playlistDraggedReceiver)
    unregisterReceiver(dismissNotificationReceiver)
    unregisterReceiver(stopReceiver)
}