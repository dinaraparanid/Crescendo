package com.paranid5.crescendo.services.stream_service.media_session

import android.content.Context
import android.os.SystemClock
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.paranid5.crescendo.R
import com.paranid5.crescendo.services.stream_service.ACTION_DISMISS
import com.paranid5.crescendo.services.stream_service.ACTION_REPEAT
import com.paranid5.crescendo.services.stream_service.ACTION_UNREPEAT
import com.paranid5.crescendo.services.stream_service.StreamService2
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine

suspend fun StreamService2.startPlaybackStatesMonitoring() =
    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        combine(
            playerProvider.isPlayingState,
            playerProvider.isRepeatingFlow,
            playerProvider.speedFlow,
        ) { isPlaying, isRepeating, speed ->
            PlaybackState(
                context = this@startPlaybackStatesMonitoring,
                isPlaying = isPlaying,
                isRepeating = isRepeating,
                currentPlaybackPosition = playerProvider.currentPosition,
                speed = speed
            )
        }.collectLatest {
            mediaSessionManager.updatePlaybackState(it)
            notificationManager.updateNotification()
        }
    }

private fun PlaybackState(
    context: Context,
    isPlaying: Boolean,
    isRepeating: Boolean,
    currentPlaybackPosition: Long,
    speed: Float,
) = PlaybackStateCompat.Builder()
    .setActions(playbackActions)
    .setCustomActions(context, isRepeating)
    .setState(
        when {
            isPlaying -> PlaybackStateCompat.STATE_PLAYING
            else -> PlaybackStateCompat.STATE_PAUSED
        },
        currentPlaybackPosition,
        speed,
        SystemClock.elapsedRealtime()
    )
    .build()

private inline val playbackActions
    get() = PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
            PlaybackStateCompat.ACTION_PLAY or
            PlaybackStateCompat.ACTION_PAUSE or
            PlaybackStateCompat.ACTION_PLAY_PAUSE or
            PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
            PlaybackStateCompat.ACTION_SEEK_TO

private fun PlaybackStateCompat.Builder.setCustomActions(
    context: Context,
    isRepeating: Boolean,
) = this
    .addCustomAction(RepeatAction(context, isRepeating))
    .addCustomAction(CancelAction(context))

private fun RepeatAction(context: Context, isRepeating: Boolean) = when {
    isRepeating -> PlaybackStateCompat.CustomAction.Builder(
        ACTION_REPEAT,
        context.getString(R.string.change_repeat),
        R.drawable.repeat
    )

    else -> PlaybackStateCompat.CustomAction.Builder(
        ACTION_UNREPEAT,
        context.getString(R.string.change_repeat),
        R.drawable.no_repeat
    )
}.build()

private fun CancelAction(context: Context) =
    PlaybackStateCompat.CustomAction.Builder(
        ACTION_DISMISS,
        context.getString(R.string.cancel),
        R.drawable.dismiss
    ).build()