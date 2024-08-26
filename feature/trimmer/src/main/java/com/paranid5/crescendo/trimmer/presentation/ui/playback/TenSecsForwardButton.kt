package com.paranid5.crescendo.trimmer.presentation.ui.playback

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.trimmer.view_model.TrimmerUiIntent

private val ButtonSize = 48.dp

@Composable
internal fun TenSecsForwardButton(
    onUiIntent: (TrimmerUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = IconButton(
    onClick = { onUiIntent(TrimmerUiIntent.Player.SeekTenSecsForward) },
    modifier = modifier.size(ButtonSize),
) {
    Icon(
        imageVector = ImageVector.vectorResource(R.drawable.ic_music_next),
        contentDescription = stringResource(R.string.ten_secs_forward),
        tint = colors.primary,
        modifier = Modifier.fillMaxSize(),
    )
}
