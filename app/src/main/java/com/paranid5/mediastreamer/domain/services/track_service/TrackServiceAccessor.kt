package com.paranid5.mediastreamer.domain.services.track_service

import android.app.Service
import android.content.Intent
import android.os.Build
import com.paranid5.mediastreamer.MainApplication
import com.paranid5.mediastreamer.data.tracks.DefaultTrack
import com.paranid5.mediastreamer.domain.ServiceAccessor

class TrackServiceAccessor(application: MainApplication) : ServiceAccessor(application) {
    private fun Intent.putPlaylistAndTrackIndexIfNotNull(
        playlist: List<DefaultTrack>?,
        trackInd: Int
    ) = apply {
        if (playlist != null) {
            putExtra(TrackService.PLAYLIST_ARG, playlist.toTypedArray())
            putExtra(TrackService.TRACK_INDEX_ARG, trackInd)
        }
    }

    private fun startTrackService(playlist: List<DefaultTrack>?, trackInd: Int) {
        val serviceIntent = Intent(appContext, TrackService::class.java)
            .putPlaylistAndTrackIndexIfNotNull(playlist, trackInd)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            appContext.startForegroundService(serviceIntent)
        else
            appContext.startService(serviceIntent)

        appContext.bindService(
            serviceIntent,
            application.trackServiceConnection,
            Service.BIND_AUTO_CREATE
        )
    }

    private fun switchPlaylist(playlist: List<DefaultTrack>?, trackInd: Int) = sendBroadcast(
        Intent(TrackService.Broadcast_SWITCH_PLAYLIST)
            .putPlaylistAndTrackIndexIfNotNull(playlist, trackInd)
    )

    private fun launchTrackService(playlist: List<DefaultTrack>?, trackInd: Int) = when {
        application.isTrackServiceConnected -> switchPlaylist(playlist, trackInd)
        else -> startTrackService(playlist, trackInd)
    }

    fun startPlaying(playlist: List<DefaultTrack>?, trackInd: Int) =
        launchTrackService(playlist, trackInd)

    fun sendSwitchToPrevTrackBroadcast() = sendBroadcast(TrackService.Broadcast_PREV_TRACK)

    fun sendSwitchToNextTrackBroadcast() = sendBroadcast(TrackService.Broadcast_NEXT_TRACK)

    fun sendSeekToBroadcast(position: Long) = sendBroadcast(
        Intent(TrackService.Broadcast_SEEK_TO)
            .putExtra(TrackService.POSITION_ARG, position)
    )

    fun sendPauseBroadcast() = sendBroadcast(TrackService.Broadcast_PAUSE)

    private fun sendResumeBroadcast() = sendBroadcast(TrackService.Broadcast_RESUME)

    fun startStreamingOrSendResumeBroadcast() = when {
        application.isTrackServiceConnected -> sendResumeBroadcast()
        else -> startTrackService(null, 0)
    }

    fun sendChangeRepeatBroadcast() = sendBroadcast(TrackService.Broadcast_CHANGE_REPEAT)
}