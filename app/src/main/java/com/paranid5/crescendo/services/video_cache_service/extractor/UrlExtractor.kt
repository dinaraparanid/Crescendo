package com.paranid5.crescendo.services.video_cache_service.extractor

import android.content.Context
import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.paranid5.crescendo.domain.caching.Formats
import com.paranid5.crescendo.domain.metadata.VideoMetadata
import com.paranid5.yt_url_extractor_kt.VideoMeta
import com.paranid5.yt_url_extractor_kt.YtFile
import com.paranid5.yt_url_extractor_kt.YtFilesNotFoundException
import com.paranid5.yt_url_extractor_kt.YtRequestTimeoutException
import com.paranid5.yt_url_extractor_kt.extractYtFilesWithMeta
import io.ktor.client.HttpClient
import kotlinx.coroutines.withTimeout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val DEFAULT_AUDIO_TAG = 140
private const val TIMEOUT = 28000L

class UrlExtractor : KoinComponent {
    private val ktorClient by inject<HttpClient>()

    suspend fun extractUrlsWithMeta(
        context: Context,
        ytUrl: String,
        format: Formats
    ) = either {
        val (ytFiles, _, videoMetaRes) = extractYtFilesWithMeta(context, ytUrl)
            .mapLeft { YtRequestTimeoutException() }
            .bind()

        val metadata = videoMetaRes.getOrDefault()

        ensure(!metadata.isLiveStream) {
            LiveStreamingNotAllowedException()
        }

        val audioUrl = ytFiles[DEFAULT_AUDIO_TAG]?.url

        ensure(audioUrl != null) {
            YtFilesNotFoundException()
        }

        when (format) {
            Formats.MP4 -> {
                val videoUrl = videoUrl(ytFiles)

                ensure(videoUrl != null) {
                    YtFilesNotFoundException()
                }

                arrayOf(audioUrl, videoUrl) to metadata
            }

            else -> arrayOf(audioUrl) to metadata
        }
    }

    private suspend inline fun extractYtFilesWithMeta(context: Context, ytUrl: String) =
        Either.catch {
            withTimeout(TIMEOUT) {
                ktorClient
                    .extractYtFilesWithMeta(context, ytUrl)
                    .getOrThrow()
            }
        }
}

fun Result<VideoMeta>.getOrDefault() =
    getOrNull()?.let(::VideoMetadata) ?: VideoMetadata()

private fun videoUrl(ytFiles: Map<Int, YtFile>) =
    sequenceOf(137, 22, 18)
        .map(ytFiles::get)
        .filterNotNull()
        .map(YtFile::url)
        .filterNotNull()
        .filter(String::isNullOrEmpty)
        .firstOrNull()