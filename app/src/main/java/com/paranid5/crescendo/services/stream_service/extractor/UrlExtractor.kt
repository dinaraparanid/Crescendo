package com.paranid5.crescendo.services.stream_service.extractor

import android.content.Context
import com.paranid5.yt_url_extractor_kt.VideoMeta
import com.paranid5.yt_url_extractor_kt.YtFilesNotFoundException
import com.paranid5.yt_url_extractor_kt.extractYtFilesWithMeta
import io.ktor.client.HttpClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val DEFAULT_AUDIO_TAG = 140

class UrlExtractor : KoinComponent {
    private val ktorClient by inject<HttpClient>()

    suspend fun extractAudioUrlWithMeta(
        context: Context,
        ytUrl: String
    ): Result<Pair<String, VideoMeta?>> {
        val extractRes = extractYtFilesWithMeta(context, ytUrl)

        val (ytFiles, liveStreamManifestsRes, videoMetaRes) =
            when (val res = extractRes.getOrNull()) {
                null -> return Result.failure(extractRes.exceptionOrNull()!!)
                else -> res
            }

        val videoMeta = videoMetaRes.getOrNull()
        val liveStreamManifests = liveStreamManifestsRes.getOrNull()

        val audioUrl = when (videoMeta?.isLiveStream) {
            true -> liveStreamManifests?.hlsManifestUrl
            else -> ytFiles[DEFAULT_AUDIO_TAG]?.url
        }

        return when (audioUrl) {
            null -> Result.failure(YtFilesNotFoundException())
            else -> Result.success(audioUrl to videoMeta)
        }
    }

    private suspend inline fun extractYtFilesWithMeta(context: Context, ytUrl: String) =
        ktorClient.extractYtFilesWithMeta(context, ytUrl)
}