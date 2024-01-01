package com.paranid5.crescendo.services.stream_service.media_session

import android.content.Context
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat

private const val TAG = "MediaSession"

fun MediaSession(
    context: Context,
    mediaSessionCallback: MediaSessionCompat.Callback,
) = MediaSessionCompat(context, TAG).apply {
    isActive = true

    @Suppress("DEPRECATION")
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) setFlags(
        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
                or MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
    )

    setCallback(mediaSessionCallback)
}