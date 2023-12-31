package com.paranid5.crescendo.services.service_controllers

import android.content.Context
import com.paranid5.crescendo.IS_PLAYING
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.storeCurrentMetadata
import com.paranid5.crescendo.data.properties.storeCurrentPlaylist
import com.paranid5.crescendo.data.properties.storeCurrentTrackIndex
import com.paranid5.crescendo.data.properties.storeCurrentUrl
import com.paranid5.crescendo.data.properties.storeRepeating
import com.paranid5.crescendo.data.properties.storeStreamPlaybackPosition
import com.paranid5.crescendo.data.properties.storeTracksPlaybackPosition
import com.paranid5.crescendo.domain.metadata.VideoMetadata
import com.paranid5.crescendo.domain.tracks.DefaultTrack
import com.paranid5.yt_url_extractor_kt.extractYtFilesWithMeta
import io.ktor.client.HttpClient
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class MediaRetrieverController : KoinComponent {
    private val storageHandler by inject<StorageHandler>()
    private val ktorClient by inject<HttpClient>()

    val currentUrlState = storageHandler.currentUrlState
    inline val currentUrl get() = currentUrlState.value

    val currentTrackIndexState = storageHandler.currentTrackIndexState
    inline val currentTrackIndex get() = currentTrackIndexState.value

    val currentPlaylistState = storageHandler.currentPlaylistState
    inline val currentPlaylist get() = currentPlaylistState.value

    val currentTrackState = storageHandler.currentTrackState
    inline val currentTrackOrNull get() = currentTrackState.value

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
        storageHandler.storeRepeating(isRepeating)

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

    suspend fun storeCurrentPlaylist(playlist: ImmutableList<DefaultTrack>) =
        storageHandler.storeCurrentPlaylist(playlist)

    suspend fun extractYtFilesWithMeta(context: Context, ytUrl: String) =
        ktorClient.extractYtFilesWithMeta(
            context = context.applicationContext,
            ytUrl = ytUrl
        )
}