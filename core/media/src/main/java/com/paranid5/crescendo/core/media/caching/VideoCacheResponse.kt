package com.paranid5.crescendo.core.media.caching

import android.content.Context
import android.os.Parcelable
import android.widget.Toast
import com.paranid5.crescendo.core.resources.R
import kotlinx.parcelize.Parcelize

@Deprecated("Will be removed")
sealed interface VideoCacheResponse : Parcelable {

    @Deprecated("Will be removed")
    @Parcelize
    data object Success : VideoCacheResponse

    @Deprecated("Will be removed")
    @Parcelize
    data class Error(
        @JvmField val httpCode: Int,
        @JvmField val description: String
    ) : VideoCacheResponse

    @Deprecated("Will be removed")
    @Parcelize
    data object Canceled : VideoCacheResponse

    @Deprecated("Will be removed")
    @Parcelize
    data object AudioConversionError : VideoCacheResponse

    @Deprecated("Will be removed")
    @Parcelize
    data object FileCreationError : VideoCacheResponse

    @Deprecated("Will be removed")
    @Parcelize
    data object ConnectionLostError : VideoCacheResponse

    @Deprecated("Will be removed")
    @Parcelize
    data object LiveStreamNotAllowed : VideoCacheResponse
}

@Deprecated("Will be removed")
fun toast(response: VideoCacheResponse, context: Context) =
    Toast.makeText(context, response.message(context), Toast.LENGTH_LONG).show()

@Deprecated("Will be removed")
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