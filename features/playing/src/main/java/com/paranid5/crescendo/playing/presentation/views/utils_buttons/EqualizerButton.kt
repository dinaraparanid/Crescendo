package com.paranid5.crescendo.playing.presentation.views.utils_buttons

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.navigation.LocalNavigator
import com.paranid5.crescendo.playing.domain.PlayingInteractor
import com.paranid5.crescendo.ui.composition_locals.playing.LocalPlayingSheetState
import com.paranid5.crescendo.utils.extensions.getBrightDominantOrPrimary
import com.paranid5.crescendo.utils.extensions.simpleShadow
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

private val EqualizerIconSize = 32.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun EqualizerButton(
    palette: Palette?,
    modifier: Modifier = Modifier,
    interactor: PlayingInteractor = koinInject()
) {
    val context = LocalContext.current
    val navigator = LocalNavigator.current
    val playingSheetState = LocalPlayingSheetState.current

    val paletteColor = palette.getBrightDominantOrPrimary()
    val scope = rememberCoroutineScope()

    Box(modifier) {
        IconButton(
            modifier = Modifier
                .simpleShadow(color = paletteColor)
                .align(Alignment.Center),
            onClick = {
                interactor.navigateToAudioEffects(context, navigator)
                scope.launch { playingSheetState?.bottomSheetState?.collapse() }
            }
        ) {
            EqualizerIcon(
                paletteColor = paletteColor,
                modifier = Modifier.size(EqualizerIconSize),
            )
        }
    }
}

@Composable
private fun EqualizerIcon(paletteColor: Color, modifier: Modifier = Modifier) =
    Icon(
        modifier = modifier,
        imageVector = ImageVector.vectorResource(R.drawable.equalizer),
        contentDescription = stringResource(R.string.equalizer),
        tint = paletteColor,
    )