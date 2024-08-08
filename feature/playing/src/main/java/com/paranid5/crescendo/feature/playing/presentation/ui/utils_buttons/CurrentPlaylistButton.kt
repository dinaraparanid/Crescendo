package com.paranid5.crescendo.feature.playing.presentation.ui.utils_buttons

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.ui.composition_locals.LocalCurrentPlaylistSheetState
import com.paranid5.crescendo.utils.extensions.simpleShadow
import kotlinx.coroutines.launch

private val IconSize = 32.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun CurrentPlaylistButton(tint: Color, modifier: Modifier = Modifier) {
    val curPlaylistSheetState = LocalCurrentPlaylistSheetState.current
    val scope = rememberCoroutineScope()

    Box(modifier) {
        IconButton(
            onClick = { scope.launch { curPlaylistSheetState?.show() } },
            modifier = Modifier
                .simpleShadow(color = tint)
                .align(Alignment.Center),
        ) {
            CurrentPlaylistIcon(
                tint = tint,
                modifier = Modifier.size(IconSize),
            )
        }
    }
}

@Composable
private fun CurrentPlaylistIcon(tint: Color, modifier: Modifier = Modifier) =
    Icon(
        modifier = modifier,
        imageVector = ImageVector.vectorResource(R.drawable.ic_playlist),
        contentDescription = stringResource(R.string.current_playlist),
        tint = tint,
    )
