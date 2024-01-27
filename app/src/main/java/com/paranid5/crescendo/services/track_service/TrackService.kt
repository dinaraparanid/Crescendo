package com.paranid5.crescendo.services.track_service

import android.content.Intent
import android.os.Build
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.current_playlist.CurrentPlaylistRepository
import com.paranid5.crescendo.domain.tracks.Track
import com.paranid5.crescendo.services.ConnectionManager
import com.paranid5.crescendo.services.SuspendService
import com.paranid5.crescendo.services.connect
import com.paranid5.crescendo.services.core.media_session.MediaSessionManager
import com.paranid5.crescendo.services.core.notification.detachNotification
import com.paranid5.crescendo.services.core.receivers.DismissNotificationReceiver
import com.paranid5.crescendo.services.core.receivers.StopReceiver
import com.paranid5.crescendo.services.disconnect
import com.paranid5.crescendo.services.track_service.media_session.MediaSessionCallback
import com.paranid5.crescendo.services.track_service.media_session.startMetadataMonitoring
import com.paranid5.crescendo.services.track_service.media_session.startPlaybackStatesMonitoring
import com.paranid5.crescendo.services.track_service.notification.NotificationManager
import com.paranid5.crescendo.services.track_service.notification.startNotificationMonitoring
import com.paranid5.crescendo.services.track_service.playback.PlayerProvider
import com.paranid5.crescendo.services.track_service.playback.playPlaylistAsync
import com.paranid5.crescendo.services.track_service.playback.startBassMonitoring
import com.paranid5.crescendo.services.track_service.playback.startEqMonitoring
import com.paranid5.crescendo.services.track_service.playback.startPlaybackEffectsMonitoring
import com.paranid5.crescendo.services.track_service.playback.startPlaybackEventLoop
import com.paranid5.crescendo.services.track_service.playback.startResumingAsync
import com.paranid5.crescendo.services.track_service.playback.startReverbMonitoring
import com.paranid5.crescendo.services.track_service.receivers.AddTrackReceiver
import com.paranid5.crescendo.services.track_service.receivers.PauseReceiver
import com.paranid5.crescendo.services.track_service.receivers.PlaylistDraggedReceiver
import com.paranid5.crescendo.services.track_service.receivers.RemoveTrackReceiver
import com.paranid5.crescendo.services.track_service.receivers.RepeatChangedReceiver
import com.paranid5.crescendo.services.track_service.receivers.ResumeReceiver
import com.paranid5.crescendo.services.track_service.receivers.SeekToNextTrackReceiver
import com.paranid5.crescendo.services.track_service.receivers.SeekToPrevTrackReceiver
import com.paranid5.crescendo.services.track_service.receivers.SeekToReceiver
import com.paranid5.crescendo.services.track_service.receivers.SwitchPlaylistReceiver
import com.paranid5.crescendo.services.track_service.receivers.registerReceivers
import com.paranid5.crescendo.services.track_service.receivers.unregisterReceiver
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val ACTION_PAUSE = "pause"
private const val ACTION_RESUME = "resume"
private const val ACTION_PREV_TRACK = "prev_track"
private const val ACTION_NEXT_TRACK = "next_track"

internal const val ACTION_REPEAT = "repeat"
internal const val ACTION_UNREPEAT = "unrepeat"
internal const val ACTION_DISMISS = "dismiss"

class TrackService : SuspendService(), KoinComponent,
    ConnectionManager by ConnectionManagerImpl() {
    companion object {
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

    private val storageHandler by inject<StorageHandler>()
    private val currentPlaylistRepository by inject<CurrentPlaylistRepository>()

    val mediaSessionManager by lazy {
        MediaSessionManager(storageHandler, currentPlaylistRepository)
    }

    val playerProvider by lazy {
        PlayerProvider(this, storageHandler, currentPlaylistRepository)
    }

    val notificationManager by lazy {
        NotificationManager(this, storageHandler, currentPlaylistRepository)
    }

    internal val commandsToActions = mapOf(
        ACTION_PAUSE to Actions.Pause,
        ACTION_RESUME to Actions.Resume,
        ACTION_PREV_TRACK to Actions.PrevTrack,
        ACTION_NEXT_TRACK to Actions.NextTrack,
        ACTION_REPEAT to Actions.Repeat,
        ACTION_UNREPEAT to Actions.Unrepeat,
        ACTION_DISMISS to Actions.Dismiss
    )

    internal val pauseReceiver = PauseReceiver(this)
    internal val resumeReceiver = ResumeReceiver(this)
    internal val switchPlaylistReceiver = SwitchPlaylistReceiver(this)
    internal val seekToReceiver = SeekToReceiver(this)
    internal val seekToNextTrackReceiver = SeekToNextTrackReceiver(this)
    internal val seekToPrevTrackReceiver = SeekToPrevTrackReceiver(this)
    internal val repeatChangedReceiver = RepeatChangedReceiver(this)
    internal val addTrackReceiver = AddTrackReceiver(this)
    internal val removeTrackReceiver = RemoveTrackReceiver(this)
    internal val playlistDraggedReceiver = PlaylistDraggedReceiver(this)
    internal val dismissNotificationReceiver = DismissNotificationReceiver(this)
    internal val stopReceiver = StopReceiver(this)

    override fun onCreate() {
        super.onCreate()
        registerReceivers()

        mediaSessionManager.initMediaSession(
            context = this,
            mediaSessionCallback = MediaSessionCallback(this)
        )

        notificationManager.initNotificationManager(playerProvider.player)

        launchMonitoringTasks()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        connect(startId)

        when (intent!!.startType) {
            TrackServiceStart.RESUME -> startResumingAsync()
            TrackServiceStart.NEW_PLAYLIST -> playPlaylistAsync()
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        disconnect()

        detachNotification()
        notificationManager.releasePlayer()

        playerProvider.releasePlayerWithEffects()
        mediaSessionManager.releaseMediaSession()

        unregisterReceiver()
    }
}

private fun TrackService.launchMonitoringTasks() {
    serviceScope.launch { startPlaybackEventLoop() }
    serviceScope.launch { startNotificationMonitoring() }
    serviceScope.launch { startPlaybackStatesMonitoring() }
    serviceScope.launch { startMetadataMonitoring() }
    serviceScope.launch { startPlaybackEffectsMonitoring() }
    serviceScope.launch { startEqMonitoring() }
    serviceScope.launch { startBassMonitoring() }
    serviceScope.launch { startReverbMonitoring() }
}

@Suppress("DEPRECATION")
internal inline val Intent.startType: TrackServiceStart
    get() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
            getSerializableExtra(TrackService.START_TYPE_ARG, TrackServiceStart::class.java)!!

        else -> getSerializableExtra(TrackService.START_TYPE_ARG) as TrackServiceStart
    }

@Suppress("DEPRECATION")
private inline val Intent.trackArgOrNull: Track?
    get() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
            getParcelableExtra(TrackService.TRACK_ARG, Track::class.java)

        else -> getParcelableExtra(TrackService.TRACK_ARG)
    }

internal inline val Intent.trackArg
    get() = trackArgOrNull!!

internal inline val Intent.trackIndexArg
    get() = getIntExtra(TrackService.TRACK_INDEX_ARG, 0)

internal inline val Intent.positionArg
    get() = getLongExtra(TrackService.POSITION_ARG, 0)