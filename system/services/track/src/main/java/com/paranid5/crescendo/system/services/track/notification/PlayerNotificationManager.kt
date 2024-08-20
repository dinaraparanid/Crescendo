package com.paranid5.crescendo.system.services.track.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.Player
import androidx.media3.common.util.NotificationUtil
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerNotificationManager
import com.paranid5.crescendo.core.media.images.getThumbnailBitmap
import com.paranid5.crescendo.core.media.images.getTrackCoverBitmapAsync
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.system.common.intent.mainActivityIntent
import com.paranid5.crescendo.system.services.track.ACTION_DISMISS
import com.paranid5.crescendo.system.services.track.ACTION_REPEAT
import com.paranid5.crescendo.system.services.track.ACTION_UNREPEAT
import com.paranid5.crescendo.system.services.track.TrackService
import com.paranid5.crescendo.utils.extensions.artistAlbum
import com.paranid5.crescendo.utils.extensions.sendAppBroadcast
import com.paranid5.system.services.common.notification.detachNotification
import com.paranid5.system.services.common.startMediaForeground
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
internal fun PlayerNotificationManager(service: TrackService) =
    PlayerNotificationManager.Builder(
        service,
        TRACKS_NOTIFICATION_ID,
        TRACKS_CHANNEL_ID
    )
        .setChannelNameResourceId(R.string.app_name)
        .setChannelDescriptionResourceId(R.string.app_name)
        .setChannelImportance(NotificationUtil.IMPORTANCE_HIGH)
        .setNotificationListener(NotificationListener(service))
        .setMediaDescriptionAdapter(MediaDescriptionProvider(service))
        .setCustomActionReceiver(CustomActionsReceiver(service))
        .setFastForwardActionIconResourceId(R.drawable.ic_music_next)
        .setRewindActionIconResourceId(R.drawable.ic_music_previous)
        .setPlayActionIconResourceId(R.drawable.ic_play_filled)
        .setPauseActionIconResourceId(R.drawable.ic_pause)
        .build()
        .apply {
            setUseStopAction(false)
            setUseChronometer(false)
            setUseFastForwardAction(false)
            setUseRewindAction(false)
            setUseFastForwardActionInCompactView(false)
            setUseRewindActionInCompactView(false)

            setPriority(NotificationCompat.PRIORITY_HIGH)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setMediaSessionToken(service.mediaSessionManager.sessionToken)
        }

@OptIn(UnstableApi::class)
private fun NotificationListener(service: TrackService) =
    object : PlayerNotificationManager.NotificationListener {
        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            super.onNotificationCancelled(notificationId, dismissedByUser)
            service.detachNotification()
        }

        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean
        ) {
            super.onNotificationPosted(notificationId, notification, ongoing)
            service.startMediaForeground(notificationId, notification)
        }
    }

@OptIn(UnstableApi::class)
private fun MediaDescriptionProvider(service: TrackService) =
    object : PlayerNotificationManager.MediaDescriptionAdapter {
        override fun getCurrentContentTitle(player: Player) =
            service.trackTitle ?: service.getString(R.string.unknown_track)

        override fun getCurrentContentText(player: Player) =
            service.trackArtistAlbum ?: service.getString(R.string.unknown_artist)

        override fun createCurrentContentIntent(player: Player) =
            PendingIntent.getActivity(
                service,
                0,
                service.mainActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            service.serviceScope.launch(Dispatchers.IO) {
                callback.onBitmap(
                    service
                        .notificationManager
                        .getTrackCoverBitmapAsync(service)
                        .await()
                )
            }

            return null
        }
    }

@OptIn(UnstableApi::class)
private fun CustomActionsReceiver(service: TrackService) =
    object : PlayerNotificationManager.CustomActionReceiver {
        override fun createCustomActions(
            context: Context,
            instanceId: Int
        ) = mutableMapOf(
            ACTION_REPEAT to RepeatActionCompat(service),
            ACTION_UNREPEAT to UnrepeatActionCompat(service),
            ACTION_DISMISS to DismissNotificationActionCompat(service)
        )

        override fun getCustomActions(player: Player) =
            CustomActions(player.repeatMode)

        override fun onCustomAction(player: Player, action: String, intent: Intent) {
            service.sendAppBroadcast(service.commandsToActions[action]!!.playbackAction)
        }
    }

private inline val TrackService.trackTitle
    get() = track?.title

private inline val TrackService.trackArtistAlbum
    get() = track?.artistAlbum

private inline val TrackService.track
    get() = notificationManager.currentTrackState.value

private suspend inline fun NotificationManager.getTrackCoverBitmapAsync(context: Context) =
    currentTrackState.value
        ?.let { getTrackCoverBitmapAsync(context = context, path = it.path) }
        ?: coroutineScope { async(Dispatchers.IO) { getThumbnailBitmap(context) } }

private fun CustomActions(repeatMode: Int) = mutableListOf(
    when (repeatMode) {
        Player.REPEAT_MODE_ONE -> ACTION_REPEAT
        else -> ACTION_UNREPEAT
    },
    ACTION_DISMISS
)