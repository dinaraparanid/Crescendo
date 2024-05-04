package com.paranid5.crescendo.playing.presentation.views.properties

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.playing.presentation.PlayingViewModel
import com.paranid5.crescendo.playing.presentation.properties.compose.collectCurrentTrackAsState
import com.paranid5.crescendo.ui.track.item.TrackPropertiesButton
import com.paranid5.crescendo.utils.extensions.getBrightDominantOrPrimary
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun PropertiesButton(
    audioStatus: AudioStatus,
    palette: Palette?,
    modifier: Modifier = Modifier,
    viewModel: PlayingViewModel = koinViewModel(),
) {
    val currentTrack by viewModel.collectCurrentTrackAsState()

    when (audioStatus) {
        AudioStatus.STREAMING -> VideoPropertiesButton(
            tint = palette.getBrightDominantOrPrimary(),
            modifier = modifier
        )

        AudioStatus.PLAYING ->
            if (currentTrack != null)
                TrackPropertiesButton(
                    track = currentTrack!!,
                    tint = palette.getBrightDominantOrPrimary(),
                    modifier = modifier,
                    iconModifier = Modifier
                        .height(48.dp)
                        .width(24.dp),
                )
    }
}