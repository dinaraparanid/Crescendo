package com.paranid5.crescendo.trimmer.view_model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.focus.FocusState
import com.paranid5.crescendo.core.common.caching.Formats
import com.paranid5.crescendo.core.common.trimming.FadeDurations
import com.paranid5.crescendo.core.common.trimming.PitchAndSpeed
import com.paranid5.crescendo.core.common.trimming.TrimRange
import com.paranid5.crescendo.trimmer.domain.entities.ShownEffects
import com.paranid5.crescendo.trimmer.presentation.CONTROLLER_CIRCLE_CENTER
import com.paranid5.crescendo.trimmer.presentation.CONTROLLER_CIRCLE_RADIUS
import com.paranid5.crescendo.trimmer.presentation.CONTROLLER_RECT_OFFSET
import com.paranid5.crescendo.ui.foundation.UiState
import com.paranid5.crescendo.ui.foundation.fold
import com.paranid5.crescendo.ui.track.ui_state.TrackUiState
import com.paranid5.crescendo.utils.extensions.safeDiv
import com.paranid5.crescendo.utils.extensions.timeFormat
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class TrimmerState(
    val trackState: UiState<TrackUiState> = UiState.Initial,
    val amplitudes: ImmutableList<Int> = persistentListOf(),
    val shownEffects: ShownEffects = ShownEffects.NONE,
    val playbackPositions: PlaybackPositions = PlaybackPositions(),
    val playbackProperties: PlaybackProperties = PlaybackProperties(),
    val waveformProperties: WaveformZoomProperties = WaveformZoomProperties(),
    val fileSaveDialogProperties: FileSaveDialogProperties = FileSaveDialogProperties(),
    @IgnoredOnParcel val focusEvent: FocusState? = null,
) : Parcelable {

    @Parcelize
    @Immutable
    data class PlaybackPositions(
        val startPosInMillis: Long = InitialPosition,
        val endPosInMillis: Long = InitialPosition,
        val playbackPosInMillis: Long = InitialPosition,
        val fadeInSecs: Long = MinFade,
        val fadeOutSecs: Long = MinFade,
    ) : Parcelable {
        companion object {
            internal const val InitialPosition = 0L
            internal const val MinFade = 0L
            internal const val MaxFade = 30L
        }

        @IgnoredOnParcel
        val trimmedDurationInMillis = endPosInMillis - startPosInMillis

        @IgnoredOnParcel
        val trimRange = TrimRange(
            startPointMillis = startPosInMillis,
            totalDurationMillis = trimmedDurationInMillis,
        )

        @IgnoredOnParcel
        val fadeDurations = FadeDurations(
            fadeInSecs = fadeInSecs,
            fadeOutSecs = fadeOutSecs,
        )

        @IgnoredOnParcel
        val playbackText = playbackPosInMillis.timeFormat
    }

    @Parcelize
    @Immutable
    data class PlaybackProperties(
        val isPlayerInitialized: Boolean = false,
        val isPlaybackTaskFinished: Boolean = false,
        val isPlaying: Boolean = false,
        val pitch: Float = InitialPitch,
        val speed: Float = InitialSpeed,
    ) : Parcelable {
        companion object {
            internal const val InitialPitch = 1F
            internal const val InitialSpeed = 1F
            internal const val MinPitch = 0.5F
            internal const val MinSpeed = 0.5F
            internal const val MaxPitch = 2F
            internal const val MaxSpeed = 2F
        }

        @IgnoredOnParcel
        val playbackAlpha = if (isPlaying) 1F else 0F

        @IgnoredOnParcel
        val pitchAndSpeed = PitchAndSpeed(pitch, speed)
    }

    @Parcelize
    @Immutable
    data class WaveformZoomProperties(
        val zoomLevel: Int = InitialZoomLevel,
        val zoomSteps: Int = InitialZoomLevel,
    ) : Parcelable {
        companion object {
            internal const val InitialZoomLevel = 0
        }

        @IgnoredOnParcel
        val canZoomIn = zoomLevel < zoomSteps

        @IgnoredOnParcel
        val canZoomOut = zoomLevel > InitialZoomLevel

        @IgnoredOnParcel
        val scrollRatio = zoomSteps + 1 - zoomLevel
    }

    @Parcelize
    @Immutable
    data class FileSaveDialogProperties(
        val isDialogVisible: Boolean = false,
        val filename: String = "",
        val selectedSaveOptionIndex: Int = 0,
    ) : Parcelable {
        @IgnoredOnParcel
        val audioFormat = Formats.entries[selectedSaveOptionIndex]

        @IgnoredOnParcel
        val isSaveButtonClickable = filename.isNotBlank()
    }

    @IgnoredOnParcel
    val trackDurationInMillis = trackState.fold(
        ifEmpty = { 0L },
        ifPresent = TrackUiState::durationMillis,
    )

    @IgnoredOnParcel
    val trackDurationInSeconds = trackDurationInMillis / 1000

    @IgnoredOnParcel
    val startOffset = playbackPositions.startPosInMillis safeDiv trackDurationInMillis

    @IgnoredOnParcel
    val endOffset = playbackPositions.endPosInMillis safeDiv trackDurationInMillis

    @IgnoredOnParcel
    val playbackOffset = playbackPositions.playbackPosInMillis safeDiv trackDurationInMillis

    fun waveformWidth(spikeWidthRatio: Int): Int {
        val zoomAmplification = 1 shl waveformProperties.run { zoomSteps - zoomLevel }
        return (trackDurationInSeconds * spikeWidthRatio / zoomAmplification).toInt()
    }

    fun waveformMaxWidth(spikeWidthRatio: Int) =
        (trackDurationInSeconds * spikeWidthRatio).toInt()

    fun playbackControllerOffset(spikeWidthRatio: Int): Float {
        val controllerCircleStartPos = CONTROLLER_CIRCLE_CENTER / 2

        val waveformTotalWidth = waveformWidth(spikeWidthRatio)

        val waveformTotalWidthWithoutControllerBounds =
            waveformTotalWidth - CONTROLLER_CIRCLE_RADIUS - CONTROLLER_RECT_OFFSET

        val controllerOffsetWithoutBounds =
            playbackOffset * waveformTotalWidthWithoutControllerBounds

        return controllerCircleStartPos + controllerOffsetWithoutBounds + CONTROLLER_RECT_OFFSET
    }

}
