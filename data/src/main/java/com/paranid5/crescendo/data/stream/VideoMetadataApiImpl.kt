package com.paranid5.crescendo.data.stream

import android.content.Context
import arrow.core.Either
import arrow.core.flatMap
import com.paranid5.crescendo.core.media.metadata.VideoMetadata.fromYtMeta
import com.paranid5.crescendo.domain.stream.VideoMetadataApi
import com.paranid5.crescendo.utils.extensions.toEither
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
        extractYtFilesWithMeta(url).flatMap { it.videoMeta.map(::fromYtMeta).toEither() }

    private suspend inline fun extractYtFilesWithMeta(url: String) =
        Either.catch {
            withTimeout(RequestTimeout) {
                ktorClient
                    .extractYtFilesWithMeta(context, url)
                    .getOrThrow()
            }
        }
}
