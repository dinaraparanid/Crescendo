package com.paranid5.crescendo.presentation.main.playing.views.playback_buttons

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.R
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.storeAudioStatus
import com.paranid5.crescendo.domain.media.AudioStatus
import com.paranid5.crescendo.presentation.main.playing.PlayingUIHandler
import com.paranid5.crescendo.presentation.ui.extensions.getLightMutedOrPrimary
import com.paranid5.crescendo.presentation.ui.extensions.simpleShadow
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun PrevButton(
    audioStatus: AudioStatus,
    palette: Palette?,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    playingUIHandler: PlayingUIHandler = koinInject(),
    storageHandler: StorageHandler = koinInject()
) {
    val paletteColor = palette.getLightMutedOrPrimary()
    val coroutineScope = rememberCoroutineScope()

    IconButton(
        enabled = enabled,
        modifier = modifier.simpleShadow(color = paletteColor),
        onClick = {
            coroutineScope.launch {
                storageHandler.storeAudioStatus(audioStatus)
            }

            playingUIHandler.sendOnPrevButtonClickedBroadcast(audioStatus)
        }
    ) {
        PrevIcon(
            paletteColor = paletteColor,
            modifier = Modifier
                .width(100.dp)
                .height(50.dp)
        )
    }
}

@Composable
private fun PrevIcon(paletteColor: Color, modifier: Modifier = Modifier) =
    Icon(
        painter = painterResource(R.drawable.prev_track),
        contentDescription = stringResource(R.string.ten_secs_back),
        tint = paletteColor,
        modifier = modifier
    )