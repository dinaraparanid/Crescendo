package com.paranid5.crescendo.presentation.main.playing.views.utils_buttons

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
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.domain.media.AudioStatus
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.presentation.main.playing.PlayingViewModel
import com.paranid5.crescendo.presentation.main.playing.properties.compose.collectAudioStatusAsState
import com.paranid5.crescendo.presentation.main.playing.properties.compose.collectIsRepeatingAsState
import com.paranid5.crescendo.presentation.ui.extensions.getLightMutedOrPrimary
import com.paranid5.crescendo.presentation.ui.extensions.simpleShadow
import com.paranid5.crescendo.services.stream_service.StreamServiceAccessor
import com.paranid5.crescendo.services.stream_service.sendChangeRepeatBroadcast
import com.paranid5.crescendo.services.track_service.TrackServiceAccessor
import org.koin.compose.koinInject

@Composable
fun RepeatButton(
    palette: Palette?,
    modifier: Modifier = Modifier,
    viewModel: PlayingViewModel = koinActivityViewModel(),
    streamServiceAccessor: StreamServiceAccessor = koinInject(),
    trackServiceAccessor: TrackServiceAccessor = koinInject()
) {
    val paletteColor = palette.getLightMutedOrPrimary()
    val audioStatus by viewModel.collectAudioStatusAsState()

    Box(modifier) {
        IconButton(
            modifier = Modifier
                .simpleShadow(color = paletteColor)
                .align(Alignment.Center),
            onClick = {
                when (audioStatus) {
                    AudioStatus.STREAMING -> streamServiceAccessor.sendChangeRepeatBroadcast()
                    AudioStatus.PLAYING -> trackServiceAccessor.sendChangeRepeatBroadcast()
                    else -> Unit
                }
            }
        ) {
            RepeatIcon(
                viewModel = viewModel,
                paletteColor = paletteColor,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
private fun RepeatIcon(
    paletteColor: Color,
    modifier: Modifier = Modifier,
    viewModel: PlayingViewModel = koinActivityViewModel(),
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