package com.paranid5.crescendo.presentation.main.playing.views.properties

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.domain.media.AudioStatus
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.presentation.main.playing.PlayingViewModel
import com.paranid5.crescendo.presentation.main.playing.properties.compose.collectCurrentTrackAsState
import com.paranid5.crescendo.presentation.main.tracks.views.TrackPropertiesButton
import com.paranid5.crescendo.presentation.ui.extensions.getLightMutedOrPrimary

@Composable
fun PropertiesButton(
    audioStatus: AudioStatus,
    palette: Palette?,
    modifier: Modifier = Modifier,
    viewModel: PlayingViewModel = koinActivityViewModel(),
) {
    val currentTrack by viewModel.collectCurrentTrackAsState()

    when (audioStatus) {
        AudioStatus.STREAMING -> VideoPropertiesButton(
            tint = palette.getLightMutedOrPrimary(),
            modifier = modifier
        )

        AudioStatus.PLAYING ->
            if (currentTrack != null)
                TrackPropertiesButton(
                    track = currentTrack!!,
                    tint = palette.getLightMutedOrPrimary(),
                    modifier = modifier,
                    iconModifier = Modifier
                        .height(50.dp)
                        .width(25.dp),
                )
    }
}