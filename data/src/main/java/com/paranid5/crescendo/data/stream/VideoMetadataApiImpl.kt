package com.paranid5.crescendo.data.stream

import android.content.Context
import arrow.core.Either
import com.paranid5.crescendo.core.common.metadata.VideoMetadata
import com.paranid5.crescendo.core.media.metadata.VideoMetadata.fromYtMeta
import com.paranid5.crescendo.domain.stream.VideoMetadataApi
import com.paranid5.yt_url_extractor_kt.VideoMeta
import com.paranid5.yt_url_extractor_kt.extractYtFilesWithMeta
import io.ktor.client.HttpClient
import kotlinx.coroutines.withTimeout

internal class VideoMetadataApiImpl(
    private val ktorClient: HttpClient,
    private val context: Context,
) : VideoMetadataApi {
    private companion object {
        private const val RequestTimeout = 28_000L
    }

    override suspend fun getVideoMetadata(url: String) =
        extractYtFilesWithMeta(url).map { it.videoMeta.getOrDefault() }

    private suspend inline fun extractYtFilesWithMeta(url: String) =
        Either.catch {
            withTimeout(RequestTimeout) {
                ktorClient
                    .extractYtFilesWithMeta(context, url)
                    .getOrThrow()
            }
        }
}

private fun Result<VideoMeta>.getOrDefault() =
    fold(onSuccess = ::fromYtMeta, onFailure = { VideoMetadata() })
