package com.paranid5.crescendo.services.track_service.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.paranid5.crescendo.services.track_service.TrackService
import com.paranid5.crescendo.services.track_service.playback.replacePlaylistAsync
import com.paranid5.crescendo.services.track_service.playlistArg
import com.paranid5.crescendo.services.track_service.trackIndexArg

fun PlaylistDraggedReceiver(service: TrackService) =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val newPlaylist = intent.playlistArg
            val newTrackInd = intent.trackIndexArg
            service.replacePlaylistAsync(newPlaylist, newTrackInd)
        }
    }