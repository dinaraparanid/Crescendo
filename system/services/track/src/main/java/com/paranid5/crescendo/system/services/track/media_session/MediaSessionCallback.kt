package com.paranid5.crescendo.system.services.track.media_session

import android.content.Intent
import android.content.Intent.EXTRA_KEY_EVENT
import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_MEDIA_NEXT
import android.view.KeyEvent.KEYCODE_MEDIA_PAUSE
import android.view.KeyEvent.KEYCODE_MEDIA_PLAY
import android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.ConnectionResult
import androidx.media3.session.MediaSession.ConnectionResult.AcceptedResultBuilder
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.paranid5.crescendo.system.services.track.TrackService
import com.paranid5.crescendo.system.services.track.playback.isRepeating
import com.paranid5.crescendo.system.services.track.playback.pauseAsync
import com.paranid5.crescendo.system.services.track.playback.resumeAsync
import com.paranid5.crescendo.system.services.track.playback.seekToNextTrackAsync
import com.paranid5.crescendo.system.services.track.playback.seekToPrevTrackAsync
import com.paranid5.crescendo.utils.extensions.getParcelableCompat
import com.paranid5.crescendo.utils.extensions.sendAppBroadcast

internal fun MediaSessionCallback(service: TrackService) =
    object : MediaSession.Callback {
        @OptIn(UnstableApi::class)
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
        ): ConnectionResult = AcceptedResultBuilder(session)
            .setAvailablePlayerCommands(ConnectionResult.DEFAULT_PLAYER_COMMANDS)
            .setAvailableSessionCommands(
                ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()
                    .add(RepeatSessionCommand)
                    .add(UnrepeatSessionCommand)
                    .add(CancelSessionCommand)
                    .build()
            )
            .build()

        override fun onPostConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
        ) {
            super.onPostConnect(session, controller)

            service
                .mediaSessionManager
                .mediaSession
                .setCustomLayout(
                    listOf(
                        RepeatAction(
                            context = service,
                            isRepeating = service.playerProvider.isRepeating,
                        ),
                        CancelAction(context = service),
                    )
                )
        }

        @OptIn(UnstableApi::class)
        override fun onMediaButtonEvent(
            session: MediaSession,
            controllerInfo: MediaSession.ControllerInfo,
            intent: Intent,
        ): Boolean {
            val key = intent.extras
                ?.getParcelableCompat(EXTRA_KEY_EVENT, KeyEvent::class.java)
                ?: return false

            // TODO: рандомно приходит KEYCODE_MEDIA_NEXT
            // для bluetooth наушников, когда они начинают садиться
            return when (key.keyCode) {
                KEYCODE_MEDIA_PLAY -> service.resumeAsync().let { true }
                KEYCODE_MEDIA_PAUSE -> service.pauseAsync().let { true }
                KEYCODE_MEDIA_NEXT -> service.seekToNextTrackAsync().let { true }
                KEYCODE_MEDIA_PREVIOUS -> service.seekToPrevTrackAsync().let { true }
                else -> false
            }
        }

        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle,
        ): ListenableFuture<SessionResult> {
            service.sendAppBroadcast(
                service.commandsToActions[customCommand.customAction]!!.playbackAction
            )

            return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
        }
    }
