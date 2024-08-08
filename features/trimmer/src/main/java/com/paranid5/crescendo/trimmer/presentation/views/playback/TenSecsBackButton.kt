package com.paranid5.crescendo.trimmer.presentation.views.playback

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.media3.common.Player
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.trimmer.domain.player.seekTenSecsBack
import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectStartPosInMillisAsState
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun TenSecsBackButton(
    player: Player,
    modifier: Modifier = Modifier,
    viewModel: TrimmerViewModel = koinViewModel(),
) {
    val startPos by viewModel.collectStartPosInMillisAsState()

    IconButton(
        onClick = { player.seekTenSecsBack(startPos) },
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_music_previous),
            contentDescription = stringResource(id = R.string.ten_secs_back),
            tint = colors.primary,
            modifier = Modifier.fillMaxSize()
        )
    }
}