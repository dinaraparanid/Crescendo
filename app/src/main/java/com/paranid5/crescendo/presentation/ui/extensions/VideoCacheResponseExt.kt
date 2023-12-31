package com.paranid5.crescendo.presentation.ui.extensions

import android.content.Context
import android.widget.Toast
import com.paranid5.crescendo.R
import com.paranid5.crescendo.domain.caching.VideoCacheResponse

fun toast(response: VideoCacheResponse, context: Context) =
    Toast.makeText(context, response.message(context), Toast.LENGTH_LONG).show()

private fun VideoCacheResponse.message(context: Context) = when (this) {
    is VideoCacheResponse.Error -> {
        val (httpCode, description) = this
        "${context.getString(R.string.error)} $httpCode: $description"
    }

    VideoCacheResponse.Success ->
        context.getString(R.string.video_cached)

    VideoCacheResponse.Canceled ->
        context.getString(R.string.video_canceled)

    VideoCacheResponse.AudioConversionError ->
        context.getString(R.string.audio_conversion_error)

    VideoCacheResponse.FileCreationError ->
        context.getString(R.string.file_creation_error)

    VideoCacheResponse.ConnectionLostError ->
        context.getString(R.string.connection_lost)

    VideoCacheResponse.LiveStreamNotAllowed ->
        context.getString(R.string.livestream_not_cache)
}