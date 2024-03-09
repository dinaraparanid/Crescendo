package com.paranid5.crescendo.presentation.main.playing.views.utils_buttons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.navigation.LocalNavController
import com.paranid5.crescendo.core.impl.presentation.composition_locals.playing.LocalPlayingSheetState
import com.paranid5.crescendo.presentation.main.playing.PlayingUIHandler
import com.paranid5.crescendo.utils.extensions.getLightMutedOrPrimary
import com.paranid5.crescendo.utils.extensions.simpleShadow
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EqualizerButton(
    palette: Palette?,
    modifier: Modifier = Modifier,
    playingUIHandler: PlayingUIHandler = koinInject()
) {
    val context = LocalContext.current
    val navHostController = com.paranid5.crescendo.navigation.LocalNavController.current
    val playingSheetState = LocalPlayingSheetState.current

    val paletteColor = palette.getLightMutedOrPrimary()
    val scope = rememberCoroutineScope()

    Box(modifier) {
        IconButton(
            modifier = Modifier
                .simpleShadow(color = paletteColor)
                .align(Alignment.Center),
            onClick = {
                playingUIHandler.navigateToAudioEffects(context, navHostController)
                scope.launch { playingSheetState?.bottomSheetState?.collapse() }
            }
        ) {
            EqualizerIcon(
                paletteColor = paletteColor,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
private fun EqualizerIcon(paletteColor: Color, modifier: Modifier = Modifier) =
    Icon(
        modifier = modifier,
        painter = painterResource(R.drawable.equalizer),
        contentDescription = stringResource(R.string.equalizer),
        tint = paletteColor
    )