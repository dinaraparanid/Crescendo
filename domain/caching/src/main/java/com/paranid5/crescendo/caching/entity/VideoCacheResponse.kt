package com.paranid5.crescendo.caching.entity

import android.content.Context
import android.os.Parcelable
import android.widget.Toast
import com.paranid5.crescendo.core.resources.R
import kotlinx.parcelize.Parcelize

sealed interface VideoCacheResponse : Parcelable {

    @Parcelize
    data object Success : VideoCacheResponse

    @Parcelize
    data class Error(
        @JvmField val httpCode: Int,
        @JvmField val description: String,
    ) : VideoCacheResponse

    @Parcelize
    data object Canceled : VideoCacheResponse

    @Parcelize
    data object AudioConversionError : VideoCacheResponse

    @Parcelize
    data object FileCreationError : VideoCacheResponse

    @Parcelize
    data object ConnectionLostError : VideoCacheResponse

    @Parcelize
    data object LiveStreamNotAllowed : VideoCacheResponse
}

@Deprecated("Перенести по месту использования")
fun toast(response: VideoCacheResponse, context: Context) =
    Toast.makeText(context, response.message(context), Toast.LENGTH_LONG).show()

@Deprecated("Перенести по месту использования")
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
