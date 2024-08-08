package com.paranid5.crescendo.feature.playing.presentation.ui.utils_buttons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.utils.extensions.simpleShadow

private val EqualizerIconSize = 32.dp

@Composable
internal fun AudioEffectsButton(
    tint: Color,
    modifier: Modifier = Modifier,
    showAudioEffects: () -> Unit,
) = Box(modifier) {
    IconButton(
        modifier = Modifier
            .simpleShadow(color = tint)
            .align(Alignment.Center),
        onClick = showAudioEffects,
    ) {
        EqualizerIcon(
            tint = tint,
            modifier = Modifier.size(EqualizerIconSize),
        )
    }
}

@Composable
private fun EqualizerIcon(tint: Color, modifier: Modifier = Modifier) =
    Icon(
        modifier = modifier,
        imageVector = ImageVector.vectorResource(R.drawable.equalizer),
        contentDescription = stringResource(R.string.equalizer),
        tint = tint,
    )
