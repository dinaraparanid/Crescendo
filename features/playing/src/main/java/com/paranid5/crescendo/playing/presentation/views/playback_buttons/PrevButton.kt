package com.paranid5.crescendo.playing.presentation.views.playback_buttons

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
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.storeAudioStatus
import com.paranid5.crescendo.playing.domain.PlayingInteractor
import com.paranid5.crescendo.utils.extensions.getBrightDominantOrPrimary
import com.paranid5.crescendo.utils.extensions.simpleShadow
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
internal fun PrevButton(
    audioStatus: AudioStatus,
    palette: Palette?,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    playingUIHandler: PlayingInteractor = koinInject(),
    storageRepository: StorageRepository = koinInject()
) {
    val paletteColor = palette.getBrightDominantOrPrimary()
    val coroutineScope = rememberCoroutineScope()

    IconButton(
        enabled = enabled,
        modifier = modifier.simpleShadow(color = paletteColor),
        onClick = {
            coroutineScope.launch {
                storageRepository.storeAudioStatus(audioStatus)
            }

            playingUIHandler.sendOnPrevButtonClickedBroadcast(audioStatus)
        }
    ) {
        PrevIcon(
            paletteColor = paletteColor,
            modifier = Modifier
                .width(128.dp)
                .height(64.dp)
        )
    }
}

@Composable
private fun PrevIcon(paletteColor: Color, modifier: Modifier = Modifier) =
    Icon(
        painter = painterResource(R.drawable.previous_track),
        contentDescription = stringResource(R.string.ten_secs_back),
        tint = paletteColor,
        modifier = modifier
    )