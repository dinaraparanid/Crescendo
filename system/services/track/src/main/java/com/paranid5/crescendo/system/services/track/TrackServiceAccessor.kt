package com.paranid5.crescendo.system.services.track

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.paranid5.crescendo.core.common.tracks.DefaultTrack
import com.paranid5.crescendo.core.impl.di.TRACK_SERVICE_CONNECTION
import com.paranid5.crescendo.core.impl.tracks.DefaultTrackModel
import com.paranid5.crescendo.domain.interactor.tracks.TrackServiceInteractor
import com.paranid5.crescendo.domain.interactor.tracks.TrackServiceStart
import com.paranid5.crescendo.system.common.broadcast.StreamServiceBroadcasts
import com.paranid5.crescendo.system.common.broadcast.TrackServiceBroadcasts
import com.paranid5.system.services.common.ServiceAccessor
import com.paranid5.system.services.common.ServiceAccessorImpl
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class TrackServiceAccessor(context: Context) : KoinComponent,
    TrackServiceInteractor,
    ServiceAccessor by ServiceAccessorImpl(context) {
    private companion object {
        private val TAG = TrackServiceAccessor::class.simpleName!!
    }

    private val isTrackServiceConnectedState by inject<MutableStateFlow<Boolean>>(
        named(TRACK_SERVICE_CONNECTION)
    )

    private inline val isTrackServiceConnected
        get() = isTrackServiceConnectedState.value

    private fun startTrackService(startType: TrackServiceStart) {
        Log.d(TAG, "send start TrackService $startType")

        val serviceIntent = Intent(appContext, TrackService::class.java)
            .putStartType(startType)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            appContext.startForegroundService(serviceIntent)
        else
            appContext.startService(serviceIntent)
    }

    private fun switchPlaylist(startType: TrackServiceStart) {
        Log.d(TAG, "send switch playlist")

        sendBroadcast(
            Intent(TrackServiceBroadcasts.Broadcast_SWITCH_PLAYLIST)
                .putExtra(TrackServiceBroadcasts.START_TYPE_ARG, startType)
        )
    }

    override fun addToPlaylist(track: DefaultTrack) {
        Log.d(TAG, "Send track $track to playlist")
        sendBroadcast(Intent(TrackServiceBroadcasts.Broadcast_ADD_TRACK).putTrack(track))
    }

    override fun removeFromPlaylist(trackInd: Int) {
        Log.d(TAG, "Send remove track at $trackInd")

        sendBroadcast(
            Intent(TrackServiceBroadcasts.Broadcast_REMOVE_TRACK)
                .putExtra(TrackServiceBroadcasts.TRACK_INDEX_ARG, trackInd)
        )
    }

    override fun updatePlaylistAfterDrag() {
        Log.d(TAG, "Send update playlist after drag")
        sendBroadcast(Intent(TrackServiceBroadcasts.Broadcast_PLAYLIST_DRAGGED))
    }

    private fun launchTrackService(startType: TrackServiceStart) = when {
        isTrackServiceConnected -> switchPlaylist(startType)
        else -> startTrackService(startType)
    }

    private fun stopStreamService() =
        sendBroadcast(StreamServiceBroadcasts.Broadcast_STOP)

    override fun startPlaying(startType: TrackServiceStart) {
        stopStreamService()
        launchTrackService(startType)
    }

    override fun sendSwitchToPrevTrackBroadcast() =
        sendBroadcast(TrackServiceBroadcasts.Broadcast_PREV_TRACK)

    override fun sendSwitchToNextTrackBroadcast() =
        sendBroadcast(TrackServiceBroadcasts.Broadcast_NEXT_TRACK)

    override fun sendSeekToBroadcast(position: Long) = sendBroadcast(
        Intent(TrackServiceBroadcasts.Broadcast_SEEK_TO)
            .putExtra(TrackServiceBroadcasts.POSITION_ARG, position)
    )

    override fun sendPauseBroadcast() =
        sendBroadcast(TrackServiceBroadcasts.Broadcast_PAUSE)

    private fun sendResumeBroadcast() =
        sendBroadcast(TrackServiceBroadcasts.Broadcast_RESUME)

    override fun startStreamingOrSendResumeBroadcast() {
        stopStreamService()

        when {
            isTrackServiceConnected -> sendResumeBroadcast()
            else -> startTrackService(TrackServiceStart.RESUME)
        }
    }

    override fun sendChangeRepeatBroadcast() =
        sendBroadcast(TrackServiceBroadcasts.Broadcast_REPEAT_CHANGED)
}

private fun Intent.putStartType(startType: TrackServiceStart) =
    apply { putExtra(TrackServiceBroadcasts.START_TYPE_ARG, startType) }

private fun Intent.putTrack(track: DefaultTrack) =
    apply { putExtra(TrackServiceBroadcasts.TRACK_ARG, DefaultTrackModel(track)) }