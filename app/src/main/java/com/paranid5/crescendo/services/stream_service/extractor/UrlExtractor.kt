package com.paranid5.crescendo.services.stream_service.extractor

import android.content.Context
import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.paranid5.crescendo.domain.metadata.VideoMetadata
import com.paranid5.yt_url_extractor_kt.VideoMeta
import com.paranid5.yt_url_extractor_kt.YtFailure
import com.paranid5.yt_url_extractor_kt.YtFilesNotFoundException
import com.paranid5.yt_url_extractor_kt.YtRequestTimeoutException
import com.paranid5.yt_url_extractor_kt.extractYtFilesWithMeta
import io.ktor.client.HttpClient
import kotlinx.coroutines.withTimeout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val TIMEOUT = 28000L
private const val DEFAULT_AUDIO_TAG = 140

class UrlExtractor : KoinComponent {
    private val ktorClient by inject<HttpClient>()

    suspend fun extractAudioUrlWithMeta(
        context: Context,
        ytUrl: String
    ) = either {
        val (ytFiles, liveStreamManifestsRes, videoMetaRes) = extractYtFilesWithMeta(context, ytUrl)
            .mapLeft { YtRequestTimeoutException() }
            .bind()

        val videoMeta = videoMetaRes.getOrDefault()
        val liveStreamManifests = liveStreamManifestsRes.getOrNull()

        val audioUrl = when (videoMeta.isLiveStream) {
            true -> liveStreamManifests?.hlsManifestUrl
            else -> ytFiles[DEFAULT_AUDIO_TAG]?.url
        }

        ensure(audioUrl != null) {
            YtFilesNotFoundException()
        }

        audioUrl to videoMeta
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