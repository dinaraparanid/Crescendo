package com.paranid5.crescendo.services.service_controllers

import android.content.Context
import android.graphics.Bitmap
import com.paranid5.crescendo.IS_PLAYING
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.domain.VideoMetadata
import com.paranid5.crescendo.domain.tracks.DefaultTrack
import com.paranid5.crescendo.media.CoilUtils
import com.paranid5.yt_url_extractor_kt.extractYtFilesWithMeta
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

class MediaRetrieverController(context: Context) : KoinComponent {
    private val storageHandler by inject<StorageHandler>()
    private val coilUtils by inject<CoilUtils> { parametersOf(context) }
    private val ktorClient by inject<HttpClient>()

    val currentUrlState = storageHandler.currentUrlState
    inline val currentUrl get() = currentUrlState.value

    val currentTrackIndexState = storageHandler.currentTrackIndexState
    inline val currentTrackIndex get() = currentTrackIndexState.value

    val currentPlaylistState = storageHandler.currentPlaylistState
    inline val currentPlaylist get() = currentPlaylistState.value

    val currentTrackState = storageHandler.currentTrackState
    inline val currentTrack get() = currentTrackState.value

    val streamPlaybackPositionState = storageHandler.streamPlaybackPositionState
    inline val streamPlaybackPosition get() = streamPlaybackPositionState.value

    val tracksPlaybackPositionState = storageHandler.tracksPlaybackPositionState
    inline val tracksPlaybackPosition get() = tracksPlaybackPositionState.value

    val isRepeatingState = storageHandler.isRepeatingState
    inline val isRepeating get() = isRepeatingState.value

    val areAudioEffectsEnabledState = storageHandler.areAudioEffectsEnabledState
    inline val areAudioEffectsEnabled get() = areAudioEffectsEnabledState.value

    val pitchState = storageHandler.pitchState
    inline val pitch get() = pitchState.value

    val speedState = storageHandler.speedState
    inline val speed get() = speedState.value

    val equalizerParamState = storageHandler.equalizerParamState
    inline val equalizerParams get() = equalizerParamState.value

    val equalizerBandsState = storageHandler.equalizerBandsState
    inline val equalizerBands get() = equalizerBandsState.value

    val equalizerPresetState = storageHandler.equalizerPresetState
    inline val equalizerPreset get() = equalizerPresetState.value

    val bassStrengthState = storageHandler.bassStrengthState
    inline val bassStrength get() = bassStrengthState.value

    val reverbPresetState = storageHandler.reverbPresetState
    inline val reverbPreset get() = reverbPresetState.value

    private val _isPlayingState by inject<MutableStateFlow<Boolean>>(named(IS_PLAYING))

    val isPlayingState = _isPlayingState.asStateFlow()
    inline val isPlaying get() = isPlayingState.value

    fun setPlaying(isPlaying: Boolean) = _isPlayingState.update { isPlaying }

    suspend fun storeIsRepeating(isRepeating: Boolean) =
        storageHandler.storeIsRepeating(isRepeating)

    suspend fun storeCurrentUrl(url: String) =
        storageHandler.storeCurrentUrl(url)

    suspend fun storeCurrentTrackIndex(index: Int) =
        storageHandler.storeCurrentTrackIndex(index)

    suspend fun storeStreamPlaybackPosition(position: Long) =
        storageHandler.storeStreamPlaybackPosition(position)

    suspend fun storeTracksPlaybackPosition(position: Long) =
        storageHandler.storeTracksPlaybackPosition(position)

    suspend fun storeCurrentMetadata(videoMetadata: VideoMetadata?) =
        storageHandler.storeCurrentMetadata(videoMetadata)

    suspend fun storeCurrentPlaylist(playlist: List<DefaultTrack>) =
        storageHandler.storeCurrentPlaylist(playlist)

    suspend fun extractYtFilesWithMeta(context: Context, ytUrl: String) =
        ktorClient.extractYtFilesWithMeta(
            context = context.applicationContext,
            ytUrl = ytUrl
        )

    internal suspend inline fun getVideoCoverBitmapAsync(
        videoMetadata: VideoMetadata,
        size: Pair<Int, Int>? = null,
        crossinline bitmapSettings: (Bitmap) -> Unit = {}
    ) = coilUtils.getVideoCoverBitmapAsync(
        videoMetadata = videoMetadata,
        size = size,
        bitmapSettings = bitmapSettings
    )

    internal suspend inline fun getTrackCoverBitmapAsync(
        path: String?,
        size: Pair<Int, Int>? = null,
        crossinline bitmapSettings: (Bitmap) -> Unit = {}
    ) = coilUtils.getTrackCoverBitmapAsync(
        path = path,
        size = size,
        bitmapSettings = bitmapSettings
    )

    suspend fun getThumbnailBitmap() = coilUtils.getThumbnailBitmap()
}