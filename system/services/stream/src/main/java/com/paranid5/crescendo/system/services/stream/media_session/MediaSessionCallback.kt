package com.paranid5.crescendo.system.services.stream.media_session

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
import androidx.media3.session.MediaSession.ConnectionResult.AcceptedResultBuilder
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.paranid5.crescendo.system.services.stream.StreamService
import com.paranid5.crescendo.system.services.stream.playback.pauseAsync
import com.paranid5.crescendo.system.services.stream.playback.resumeAsync
import com.paranid5.crescendo.system.services.stream.playback.seekTenSecsBackAsync
import com.paranid5.crescendo.system.services.stream.playback.seekTenSecsForwardAsync
import com.paranid5.crescendo.utils.extensions.getParcelableCompat
import com.paranid5.crescendo.utils.extensions.sendAppBroadcast

internal fun MediaSessionCallback(service: StreamService) =
    object : MediaSession.Callback {
        @OptIn(UnstableApi::class)
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            if (session.isMediaNotificationController(controller)) {
                val sessionCommands =
                    MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS
                        .buildUpon()
                        .add(CancelAction(context = service).sessionCommand!!)
                        .build()

                val playerCommands = MediaSession.ConnectionResult.DEFAULT_PLAYER_COMMANDS
                    .buildUpon()
                    .build()

                return AcceptedResultBuilder(session)
                    .setCustomLayout(
                        listOfNotNull(
                            CancelAction(context = service),
                        )
                    )
                    .setAvailableSessionCommands(sessionCommands)
                    .setAvailablePlayerCommands(playerCommands)
                    .build()
            }

            return AcceptedResultBuilder(session).build()
        }

        @OptIn(UnstableApi::class)
        override fun onMediaButtonEvent(
            session: MediaSession,
            controllerInfo: MediaSession.ControllerInfo,
            intent: Intent
        ): Boolean {
            val key = intent.extras
                ?.getParcelableCompat(EXTRA_KEY_EVENT, KeyEvent::class)
                ?: return false

            return when (key.keyCode) {
                KEYCODE_MEDIA_PLAY -> service.resumeAsync().let { true }
                KEYCODE_MEDIA_PAUSE -> service.pauseAsync().let { true }
                KEYCODE_MEDIA_NEXT -> service.seekTenSecsForwardAsync().let { true }
                KEYCODE_MEDIA_PREVIOUS -> service.seekTenSecsBackAsync().let { true }
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
