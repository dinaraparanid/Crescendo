package com.paranid5.crescendo.system.services.track.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.paranid5.crescendo.system.services.track.TrackService
import com.paranid5.crescendo.domain.interactor.tracks.TrackServiceStart
import com.paranid5.crescendo.system.services.track.playback.pauseAsync
import com.paranid5.crescendo.system.services.track.playback.playPlaylistAsync
import com.paranid5.crescendo.system.services.track.playback.resumeAsync
import com.paranid5.crescendo.system.services.track.startType

internal fun SwitchPlaylistReceiver(service: TrackService) =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val startType = intent.startType

            when {
                startType == TrackServiceStart.NEW_PLAYLIST ->
                    service.playPlaylistAsync()

                startType == TrackServiceStart.RESUME && service.playerProvider.isPlaying ->
                    service.pauseAsync()

                else -> service.resumeAsync()
            }
        }
    }