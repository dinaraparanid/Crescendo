package com.paranid5.crescendo.feature.playing.view_model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import arrow.core.raise.nullable
import com.paranid5.crescendo.core.common.PlaybackStatus
import com.paranid5.crescendo.ui.track.ui_state.TrackUiState
import com.paranid5.feature.metadata.VideoMetadataUiState
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class PlayingState(
    val audioSessionId: Int = 0,
    val isPlaying: Boolean = false,
    val isRepeating: Boolean = false,
    val isLiked: Boolean = false,
    val actualPlaybackStatus: PlaybackStatus? = null,
    val visiblePlaybackStatus: PlaybackStatus? = null,
    val currentTrack: TrackUiState? = null,
    val currentMetadata: VideoMetadataUiState? = null,
    val playingStreamUrl: String = "",
    val streamPlaybackPosition: Long = 0,
    val trackPlaybackPosition: Long = 0,
    @IgnoredOnParcel val screenEffect: PlayingScreenEffect? = null,
) : Parcelable {

    @IgnoredOnParcel
    private val streamDurationMillis = currentMetadata?.durationMillis ?: 0

    @IgnoredOnParcel
    private val trackDurationMillis = currentTrack?.durationMillis ?: 0

    @IgnoredOnParcel
    val playbackPosition = when (actualPlaybackStatus) {
        PlaybackStatus.STREAMING -> streamPlaybackPosition
        PlaybackStatus.PLAYING -> trackPlaybackPosition
        null -> 0
    }

    @IgnoredOnParcel
    val durationMillis = when (actualPlaybackStatus) {
        PlaybackStatus.STREAMING -> streamDurationMillis
        PlaybackStatus.PLAYING -> trackDurationMillis
        null -> 0
    }

    @IgnoredOnParcel
    val isLiveStreaming =
        visiblePlaybackStatus == PlaybackStatus.STREAMING && currentMetadata?.isLiveStream == true

    @IgnoredOnParcel
    val isVisibleAudioStatusActual = nullable {
        actualPlaybackStatus.bind() == visiblePlaybackStatus.bind()
    } ?: false
}
