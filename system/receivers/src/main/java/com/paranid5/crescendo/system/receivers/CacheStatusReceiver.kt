package com.paranid5.crescendo.system.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.paranid5.crescendo.caching.entity.VideoCacheResponse
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.utils.extensions.getParcelableCompat
import com.paranid5.crescendo.utils.extensions.notNull

class CacheStatusReceiver : BroadcastReceiver() {

    companion object {
        private const val RECEIVER_LOCATION = "com.paranid5.crescendo.system.receivers"
        const val BROADCAST_VIDEO_CACHE_COMPLETED = "$RECEIVER_LOCATION.VIDEO_CACHE_COMPLETED"
        const val VIDEO_CACHE_STATUS_ARG = "video_cache_status"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val response = intent.getParcelableCompat(
            key = VIDEO_CACHE_STATUS_ARG,
            clazz = VideoCacheResponse::class,
        ).notNull

        Toast.makeText(context, response.message(context), Toast.LENGTH_LONG).show()
    }

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
}
