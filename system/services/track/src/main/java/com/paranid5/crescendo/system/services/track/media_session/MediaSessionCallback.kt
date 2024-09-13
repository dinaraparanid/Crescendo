package com.paranid5.crescendo.system.services.track.media_session

import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.ConnectionResult.AcceptedResultBuilder
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.paranid5.crescendo.system.services.track.TrackService
import com.paranid5.crescendo.system.services.track.playback.isRepeating
import com.paranid5.crescendo.utils.extensions.sendAppBroadcast

internal fun MediaSessionCallback(service: TrackService) =
    object : MediaSession.Callback {
        @OptIn(UnstableApi::class)
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            val connectionResult = super.onConnect(session, controller)

            val sessionCommands =
                MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS
                    .buildUpon()
                    .addSessionCommands(
                        listOfNotNull(
                            RepeatAction(
                                context = service,
                                isRepeating = true,
                            ).sessionCommand,
                            RepeatAction(
                                context = service,
                                isRepeating = false,
                            ).sessionCommand,
                            CancelAction(context = service).sessionCommand,
                        )
                    )
                    .build()

            val playerCommands = MediaSession.ConnectionResult.DEFAULT_PLAYER_COMMANDS
                .buildUpon()
                .build()

            return AcceptedResultBuilder(session)
                .setCustomLayout(
                    listOfNotNull(
                        RepeatAction(
                            context = service,
                            isRepeating = service.playerProvider.isRepeating,
                        ),
                        CancelAction(context = service),
                    )
                )
                .setAvailableSessionCommands(sessionCommands)
                .setAvailablePlayerCommands(playerCommands)
                .build()
        }

        override fun onPostConnect(session: MediaSession, controller: MediaSession.ControllerInfo) {
            super.onPostConnect(session, controller)
            session.setCustomLayout(
                listOf(
                    RepeatAction(
                        context = service,
                        isRepeating = service.playerProvider.isRepeating,
                    ),
                    CancelAction(context = service),
                )
            )
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