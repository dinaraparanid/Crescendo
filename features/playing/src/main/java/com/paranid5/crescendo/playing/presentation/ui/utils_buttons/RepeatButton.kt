package com.paranid5.crescendo.playing.presentation.ui.utils_buttons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.playing.presentation.properties.compose.collectAudioStatusAsState
import com.paranid5.crescendo.playing.presentation.properties.compose.collectIsRepeatingAsState
import com.paranid5.crescendo.playing.view_model.PlayingViewModel
import com.paranid5.crescendo.system.services.stream.StreamServiceAccessor
import com.paranid5.crescendo.system.services.stream.sendChangeRepeatBroadcast
import com.paranid5.crescendo.system.services.track.TrackServiceInteractor
import com.paranid5.crescendo.utils.extensions.getBrightDominantOrPrimary
import com.paranid5.crescendo.utils.extensions.simpleShadow
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
internal fun RepeatButton(
    palette: Palette?,
    modifier: Modifier = Modifier,
    viewModel: PlayingViewModel = koinViewModel(),
    streamServiceAccessor: StreamServiceAccessor = koinInject(),
    trackServiceInteractor: TrackServiceInteractor = koinInject()
) {
    val paletteColor = palette.getBrightDominantOrPrimary()
    val audioStatus by viewModel.collectAudioStatusAsState()

    Box(modifier) {
        IconButton(
            modifier = Modifier
                .simpleShadow(color = paletteColor)
                .align(Alignment.Center),
            onClick = {
                when (audioStatus) {
                    AudioStatus.STREAMING -> streamServiceAccessor.sendChangeRepeatBroadcast()
                    AudioStatus.PLAYING -> trackServiceInteractor.sendChangeRepeatBroadcast()
                    else -> Unit
                }
            }
        ) {
            RepeatIcon(
                viewModel = viewModel,
                paletteColor = paletteColor,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun RepeatIcon(
    paletteColor: Color,
    modifier: Modifier = Modifier,
    viewModel: PlayingViewModel = koinViewModel(),
) {
    val isRepeating by viewModel.collectIsRepeatingAsState()

    val icon by remember(isRepeating) {
        derivedStateOf {
            when {
                isRepeating -> R.drawable.repeat
                else -> R.drawable.no_repeat
            }
        }
    }

    Icon(
        modifier = modifier,
        painter = painterResource(icon),
        contentDescription = stringResource(R.string.change_repeat),
        tint = paletteColor
    )
}