package com.paranid5.crescendo.feature.playing.view_model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import arrow.core.raise.nullable
import com.paranid5.crescendo.core.common.PlaybackStatus
import com.paranid5.crescendo.domain.image.model.Image
import com.paranid5.crescendo.domain.image.model.ImagePath
import com.paranid5.crescendo.domain.image.model.ImageUrl
import com.paranid5.crescendo.ui.track.ui_state.TrackUiState
import com.paranid5.crescendo.utils.extensions.mapToImmutableList
import com.paranid5.crescendo.utils.extensions.orNil
import com.paranid5.feature.metadata.VideoMetadataUiState
import kotlinx.collections.immutable.ImmutableList
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
) : Parcelable {

    @IgnoredOnParcel
    private val streamDurationMillis: Long = currentMetadata?.durationMillis ?: 0

    @IgnoredOnParcel
    private val trackDurationMillis: Long = currentTrack?.durationMillis ?: 0

    @IgnoredOnParcel
    val playbackPosition: Long = when (actualPlaybackStatus) {
        PlaybackStatus.STREAMING -> streamPlaybackPosition
        PlaybackStatus.PLAYING -> trackPlaybackPosition
        null -> 0
    }

    @IgnoredOnParcel
    val durationMillis: Long = when (actualPlaybackStatus) {
        PlaybackStatus.STREAMING -> streamDurationMillis
        PlaybackStatus.PLAYING -> trackDurationMillis
        null -> 0
    }

    @IgnoredOnParcel
    val isLiveStreaming: Boolean =
        visiblePlaybackStatus == PlaybackStatus.STREAMING && currentMetadata?.isLiveStream == true

    @IgnoredOnParcel
    val isVisibleAudioStatusActual: Boolean = nullable {
        actualPlaybackStatus.bind() == visiblePlaybackStatus.bind()
    } ?: false

    @IgnoredOnParcel
    val videoCovers: ImmutableList<ImageUrl> = currentMetadata?.coversUrls
        ?.mapToImmutableList { ImageUrl((it as Image.Url).value.value) }
        .orNil()

    @IgnoredOnParcel
    val currentTrackCoverPath: ImagePath? = currentTrack?.path?.let(::ImagePath)
}
