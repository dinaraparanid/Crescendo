package com.paranid5.crescendo.services.track_service

import android.content.Intent
import android.os.Build
import android.util.Log
import com.paranid5.crescendo.MainApplication
import com.paranid5.crescendo.TRACK_SERVICE_CONNECTION
import com.paranid5.crescendo.domain.tracks.DefaultTrack
import com.paranid5.crescendo.domain.tracks.Track
import com.paranid5.crescendo.services.ServiceAccessor
import com.paranid5.crescendo.services.ServiceAccessorImpl
import com.paranid5.crescendo.services.stream_service.StreamService
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class TrackServiceAccessor(application: MainApplication) : KoinComponent,
    ServiceAccessor by ServiceAccessorImpl(application.applicationContext) {
    private companion object {
        private val TAG = TrackServiceAccessor::class.simpleName!!
    }

    private val isTrackServiceConnectedState by inject<MutableStateFlow<Boolean>>(
        named(TRACK_SERVICE_CONNECTION)
    )

    private inline val isTrackServiceConnected
        get() = isTrackServiceConnectedState.value

    private fun Intent.putTrack(track: DefaultTrack) =
        apply { putExtra(TrackService.TRACK_ARG, track) }

    private fun Intent.putPlaylistAndTrackIndexIfNotNull(
        playlist: List<Track>?,
        trackInd: Int
    ) = apply {
        if (playlist != null) {
            putExtra(TrackService.PLAYLIST_ARG, playlist.toTypedArray())
            putExtra(TrackService.TRACK_INDEX_ARG, trackInd)
        }
    }

    private fun startTrackService(playlist: List<DefaultTrack>? = null, trackInd: Int = 0) {
        Log.d(TAG, "send start TrackService")

        val serviceIntent = Intent(appContext, TrackService::class.java)
            .putPlaylistAndTrackIndexIfNotNull(playlist, trackInd)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            appContext.startForegroundService(serviceIntent)
        else
            appContext.startService(serviceIntent)
    }

    private fun switchPlaylist(playlist: List<DefaultTrack>, trackInd: Int) {
        Log.d(TAG, "send switch playlist")

        sendBroadcast(
            Intent(TrackService.Broadcast_SWITCH_PLAYLIST)
                .putPlaylistAndTrackIndexIfNotNull(playlist, trackInd)
        )
    }

    fun addToPlaylist(track: DefaultTrack) {
        Log.d(TAG, "Send track $track to playlist")
        sendBroadcast(Intent(TrackService.Broadcast_ADD_TRACK).putTrack(track))
    }

    fun removeFromPlaylist(trackInd: Int) {
        Log.d(TAG, "Send remove track at $trackInd")

        sendBroadcast(
            Intent(TrackService.Broadcast_REMOVE_TRACK)
                .putExtra(TrackService.TRACK_INDEX_ARG, trackInd)
        )
    }

    fun updatePlaylistAfterDrag(newPlaylist: List<Track>, newCurTrackIndex: Int) {
        Log.d(TAG, "Send update playlist after drag")

        sendBroadcast(
            Intent(TrackService.Broadcast_PLAYLIST_DRAGGED)
                .putPlaylistAndTrackIndexIfNotNull(newPlaylist, newCurTrackIndex)
        )
    }

    private fun launchTrackService(playlist: List<DefaultTrack>, trackInd: Int) = when {
        isTrackServiceConnected -> switchPlaylist(playlist, trackInd)
        else -> startTrackService(playlist, trackInd)
    }

    private fun stopStreamService() = sendBroadcast(StreamService.Broadcast_STOP)

    fun startPlaying(playlist: List<DefaultTrack>, trackInd: Int) {
        stopStreamService()
        launchTrackService(playlist, trackInd)
    }

    fun sendSwitchToPrevTrackBroadcast() = sendBroadcast(TrackService.Broadcast_PREV_TRACK)

    fun sendSwitchToNextTrackBroadcast() = sendBroadcast(TrackService.Broadcast_NEXT_TRACK)

    fun sendSeekToBroadcast(position: Long) = sendBroadcast(
        Intent(TrackService.Broadcast_SEEK_TO)
            .putExtra(TrackService.POSITION_ARG, position)
    )

    fun sendPauseBroadcast() = sendBroadcast(TrackService.Broadcast_PAUSE)

    private fun sendResumeBroadcast() = sendBroadcast(TrackService.Broadcast_RESUME)

    fun startStreamingOrSendResumeBroadcast() {
        stopStreamService()

        when {
            isTrackServiceConnected -> sendResumeBroadcast()
            else -> startTrackService()
        }
    }

    fun sendChangeRepeatBroadcast() = sendBroadcast(TrackService.Broadcast_REPEAT_CHANGED)
}