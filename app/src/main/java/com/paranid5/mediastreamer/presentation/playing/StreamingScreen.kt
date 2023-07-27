package com.paranid5.mediastreamer.presentation.playing

import android.content.res.Configuration
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.palette.graphics.Palette
import coil.compose.AsyncImagePainter
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.data.VideoMetadata
import com.paranid5.mediastreamer.data.tracks.Track
import com.paranid5.mediastreamer.domain.StorageHandler
import com.paranid5.mediastreamer.presentation.Screens
import com.paranid5.mediastreamer.presentation.ui.AudioStatus
import com.paranid5.mediastreamer.presentation.ui.rememberCurrentTrackCoverPainterWithPalette
import com.paranid5.mediastreamer.presentation.ui.rememberTrackCoverPainterWithPalette
import com.paranid5.mediastreamer.presentation.ui.rememberVideoCoverPainterWithPalette
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.koin.compose.koinInject

private const val BROADCAST_LOCATION = "com.paranid5.mediastreamer.presentation.playing"
const val Broadcast_CUR_POSITION_CHANGED = "$BROADCAST_LOCATION.CUR_POSITION_CHANGED"
const val CUR_POSITION_ARG = "cur_position"

@Composable
fun PlayingScreen(
    viewModel: PlayingViewModel,
    curScreenState: MutableStateFlow<Screens>,
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = koinInject()
) {
    curScreenState.update { Screens.Audio.Playing }

    val audioStatus by storageHandler.audioStatusState.collectAsState()
    val currentMetadata by storageHandler.currentMetadataState.collectAsState()
    val currentTrack by storageHandler.currentTrackState.collectAsState()

    val title = when (audioStatus) {
        AudioStatus.STREAMING -> currentMetadata?.title ?: stringResource(R.string.stream_no_name)
        AudioStatus.PLAYING -> currentTrack?.title ?: stringResource(R.string.unknown_track)
        else -> stringResource(R.string.unknown_track)
    }

    val author = when (audioStatus) {
        AudioStatus.STREAMING ->
            currentMetadata?.author ?: stringResource(R.string.unknown_streamer)

        AudioStatus.PLAYING ->
            currentTrack?.artist ?: stringResource(R.string.unknown_artist)

        else -> stringResource(R.string.unknown_artist)
    }

    val length = when (audioStatus) {
        AudioStatus.STREAMING -> currentMetadata?.lenInMillis ?: 0
        AudioStatus.PLAYING -> currentTrack?.duration ?: 0
        else -> 0
    }

    val (coilPainter, palette) = when (audioStatus) {
        AudioStatus.STREAMING -> rememberVideoCoverPainterWithPalette(
            isPlaceholderRequired = true,
            size = 1100 to 1000
        )

        else -> rememberCurrentTrackCoverPainterWithPalette(
            isPlaceholderRequired = true,
            size = 1100 to 1000
        )
    }

    when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> PlayingScreenLandscape(
            title = title,
            author = author,
            length = length,
            coilPainter = coilPainter,
            palette = palette,
            viewModel = viewModel,
            modifier = modifier,
        )

        else -> PlayingScreenPortrait(
            title = title,
            author = author,
            length = length,
            coilPainter = coilPainter,
            palette = palette,
            viewModel = viewModel,
            modifier = modifier,
        )
    }
}

@Composable
private fun PlayingScreenPortrait(
    title: String,
    author: String,
    length: Long,
    coilPainter: AsyncImagePainter,
    palette: Palette?,
    viewModel: PlayingViewModel,
    modifier: Modifier,
) = ConstraintLayout(modifier.fillMaxSize()) {
    val (
        cover,
        audioWave,
        slider,
        titleAndPropertiesButton,
        playbackButtons,
        utilsButtons
    ) = createRefs()

    VideoCover(
        modifier = Modifier
            .padding(20.dp)
            .constrainAs(cover) {
                top.linkTo(parent.top, margin = 20.dp)
                start.linkTo(parent.start, margin = 20.dp)
                end.linkTo(parent.end, margin = 20.dp)
                bottom.linkTo(audioWave.top, margin = 10.dp)
                height = Dimension.fillToConstraints
                width = Dimension.fillToConstraints
            },
        coilPainter = coilPainter,
        palette = palette
    )

    AudioWaveform(
        palette = palette,
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .height(40.dp)
            .fillMaxWidth()
            .constrainAs(audioWave) {
                centerVerticallyTo(parent)
                start.linkTo(parent.start, margin = 20.dp)
                end.linkTo(parent.end, margin = 20.dp)
            }
    )

    PlaybackSlider(
        length = length,
        palette = palette,
        modifier = Modifier.constrainAs(slider) {
            top.linkTo(audioWave.bottom, margin = 10.dp)
            start.linkTo(parent.start, margin = 20.dp)
            end.linkTo(parent.end, margin = 20.dp)
        }
    ) { curPosition, videoLength, color ->
        TimeText(curPosition, color)
        Spacer(Modifier.weight(1F))
        TimeText(videoLength, color)
    }

    TitleAndPropertiesButton(
        title = title,
        author = author,
        palette = palette,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .constrainAs(titleAndPropertiesButton) {
                top.linkTo(slider.bottom, margin = 15.dp)
                start.linkTo(parent.start, margin = 20.dp)
                end.linkTo(parent.end, margin = 20.dp)
            }
    )

    PlaybackButtons(
        playingPresenter = viewModel.presenter,
        palette = palette,
        modifier = Modifier.constrainAs(playbackButtons) {
            top.linkTo(titleAndPropertiesButton.bottom, margin = 15.dp)
            start.linkTo(parent.start, margin = 20.dp)
            end.linkTo(parent.end, margin = 20.dp)
        }
    )

    UtilsButtons(
        palette = palette,
        modifier = Modifier.constrainAs(utilsButtons) {
            top.linkTo(playbackButtons.bottom, margin = 5.dp)
            start.linkTo(parent.start, margin = 20.dp)
            end.linkTo(parent.end, margin = 20.dp)
        }
    )
}

@Composable
private fun PlayingScreenLandscape(
    title: String,
    author: String,
    length: Long,
    coilPainter: AsyncImagePainter,
    palette: Palette?,
    viewModel: PlayingViewModel,
    modifier: Modifier,
) = ConstraintLayout(modifier.fillMaxSize()) {
    val (
        cover,
        audioWave,
        propertiesButton,
        slider,
        playbackButtons,
        utilsButtons
    ) = createRefs()

    AudioWaveform(
        palette = palette,
        modifier = Modifier
            .fillMaxWidth()
            .constrainAs(audioWave) {
                top.linkTo(parent.top, margin = 8.dp)
                bottom.linkTo(slider.top, margin = 2.dp)
                height = Dimension.fillToConstraints

                start.linkTo(parent.start, margin = 20.dp)
                end.linkTo(parent.end, margin = 20.dp)
            }
    )

    VideoCover(
        modifier = Modifier.constrainAs(cover) {
            centerHorizontallyTo(parent)
            top.linkTo(parent.top, margin = 8.dp)
            bottom.linkTo(slider.top, margin = 2.dp)
            height = Dimension.fillToConstraints
        },
        coilPainter = coilPainter,
        palette = palette
    )

    PropertiesButton(
        modifier = Modifier.constrainAs(propertiesButton) {
            top.linkTo(parent.top, margin = 8.dp)
            end.linkTo(parent.end, margin = 5.dp)
        },
        palette = palette
    )

    PlaybackSlider(
        length = length,
        palette = palette,
        modifier = Modifier.constrainAs(slider) {
            top.linkTo(parent.top, margin = 0.dp)
            bottom.linkTo(parent.bottom, margin = 25.dp)
            start.linkTo(parent.start, margin = 20.dp)
            end.linkTo(parent.end, margin = 20.dp)
        }
    ) { curPosition, videoLength, color ->
        TimeText(curPosition, color)

        TitleAndAuthor(
            title = title,
            author = author,
            palette = palette,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .weight(1F, fill = true),
            textAlignment = Alignment.CenterHorizontally
        )

        TimeText(videoLength, color)
    }

    PlaybackButtons(
        playingPresenter = viewModel.presenter,
        palette = palette,
        modifier = Modifier.constrainAs(playbackButtons) {
            top.linkTo(slider.bottom, margin = 5.dp)
            start.linkTo(parent.start, margin = 20.dp)
            end.linkTo(parent.end, margin = 20.dp)
        }
    )

    UtilsButtons(
        palette = palette,
        modifier = Modifier.constrainAs(utilsButtons) {
            top.linkTo(playbackButtons.bottom, margin = 2.dp)
            start.linkTo(parent.start, margin = 20.dp)
            end.linkTo(parent.end, margin = 20.dp)
        }
    )
}