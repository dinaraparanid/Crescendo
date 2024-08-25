package com.paranid5.crescendo.trimmer.presentation.ui.playback

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.trimmer.view_model.TrimmerUiIntent

@Composable
internal fun TenSecsBackButton(
    onUiIntent: (TrimmerUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = { onUiIntent(TrimmerUiIntent.Player.SeekTenSecsBack) },
        modifier = modifier,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_music_previous),
            contentDescription = stringResource(id = R.string.ten_secs_back),
            tint = colors.primary,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
