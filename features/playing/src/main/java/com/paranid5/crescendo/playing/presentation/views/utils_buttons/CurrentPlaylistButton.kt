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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.ui.composition_locals.LocalCurrentPlaylistSheetState
import com.paranid5.crescendo.utils.extensions.getBrightDominantOrPrimary
import com.paranid5.crescendo.utils.extensions.simpleShadow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun CurrentPlaylistButton(
    palette: Palette?,
    modifier: Modifier = Modifier,
) {
    val curPlaylistSheetState = LocalCurrentPlaylistSheetState.current
    val paletteColor = palette.getBrightDominantOrPrimary()
    val scope = rememberCoroutineScope()

    Box(modifier) {
        IconButton(
            onClick = { scope.launch { curPlaylistSheetState?.show() } },
            modifier = Modifier
                .simpleShadow(color = paletteColor)
                .align(Alignment.Center),
        ) {
            CurrentPlaylistIcon(
                paletteColor = paletteColor,
                modifier = Modifier.size(32.dp),
            )
        }
    }
}

@Composable
private fun CurrentPlaylistIcon(paletteColor: Color, modifier: Modifier = Modifier) =
    Icon(
        modifier = modifier,
        painter = painterResource(R.drawable.playlist),
        contentDescription = stringResource(R.string.current_playlist),
        tint = paletteColor
    )