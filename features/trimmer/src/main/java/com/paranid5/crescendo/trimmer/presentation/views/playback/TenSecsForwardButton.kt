package com.paranid5.crescendo.trimmer.presentation.views.playback

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.trimmer.domain.player.seekTenSecsForward
import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectTrackDurationInMillisAsState
import org.koin.androidx.compose.koinViewModel

private val ButtonSize = 48.dp

@Composable
internal fun TenSecsForwardButton(
    player: Player,
    modifier: Modifier = Modifier,
    viewModel: TrimmerViewModel = koinViewModel(),
) {
    val durationInMillis by viewModel.collectTrackDurationInMillisAsState()

    IconButton(
        onClick = { player.seekTenSecsForward(durationInMillis) },
        modifier = modifier.size(ButtonSize)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_music_next),
            contentDescription = stringResource(id = R.string.ten_secs_forward),
            tint = colors.primary,
            modifier = Modifier.fillMaxSize(),
        )
    }
}