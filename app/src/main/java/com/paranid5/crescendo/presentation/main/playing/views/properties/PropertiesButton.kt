package com.paranid5.crescendo.presentation.main.playing.views.properties

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.domain.media.AudioStatus
import com.paranid5.crescendo.presentation.main.tracks.views.TrackPropertiesButton
import com.paranid5.crescendo.presentation.ui.extensions.getLightMutedOrPrimary
import org.koin.compose.koinInject

@Composable
fun PropertiesButton(
    audioStatus: AudioStatus,
    palette: Palette?,
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = koinInject()
) {
    val currentTrackMb by storageHandler.currentTrackState.collectAsState()

    when (audioStatus) {
        AudioStatus.STREAMING -> VideoPropertiesButton(
            tint = palette.getLightMutedOrPrimary(),
            modifier = modifier
        )

        AudioStatus.PLAYING -> currentTrackMb?.let { currentTrack ->
            TrackPropertiesButton(
                track = currentTrack,
                tint = palette.getLightMutedOrPrimary(),
                modifier = modifier,
                iconModifier = Modifier.height(50.dp).width(25.dp),
            )
        }
    }
}