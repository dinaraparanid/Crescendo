package com.paranid5.crescendo.system.services.track.receivers

import com.paranid5.crescendo.system.common.broadcast.TrackServiceBroadcasts
import com.paranid5.crescendo.system.services.track.TrackService
import com.paranid5.crescendo.utils.extensions.registerReceiverCompat

@Suppress("IncorrectFormatting")
internal fun TrackService.registerReceivers() {
    registerReceiverCompat(pauseReceiver, TrackServiceBroadcasts.Broadcast_PAUSE)
    registerReceiverCompat(resumeReceiver, TrackServiceBroadcasts.Broadcast_RESUME)
    registerReceiverCompat(switchPlaylistReceiver, TrackServiceBroadcasts.Broadcast_SWITCH_PLAYLIST)
    registerReceiverCompat(seekToReceiver, TrackServiceBroadcasts.Broadcast_SEEK_TO)
    registerReceiverCompat(seekToNextTrackReceiver, TrackServiceBroadcasts.Broadcast_NEXT_TRACK)
    registerReceiverCompat(seekToPrevTrackReceiver, TrackServiceBroadcasts.Broadcast_PREV_TRACK)
    registerReceiverCompat(repeatChangedReceiver, TrackServiceBroadcasts.Broadcast_REPEAT_CHANGED)
    registerReceiverCompat(addTrackReceiver, TrackServiceBroadcasts.Broadcast_ADD_TRACK)
    registerReceiverCompat(removeTrackReceiver, TrackServiceBroadcasts.Broadcast_REMOVE_TRACK)
    registerReceiverCompat(playlistDraggedReceiver, TrackServiceBroadcasts.Broadcast_PLAYLIST_DRAGGED)
    registerReceiverCompat(dismissNotificationReceiver, TrackServiceBroadcasts.Broadcast_DISMISS_NOTIFICATION)
    registerReceiverCompat(stopReceiver, TrackServiceBroadcasts.Broadcast_STOP)
}

internal fun TrackService.unregisterReceivers() {
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