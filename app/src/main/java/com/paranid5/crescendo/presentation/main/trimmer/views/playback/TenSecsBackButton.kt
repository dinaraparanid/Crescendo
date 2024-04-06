package com.paranid5.crescendo.presentation.main.trimmer.views.playback

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
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.player.seekTenSecsBack
import com.paranid5.crescendo.presentation.main.trimmer.properties.compose.collectStartPosInMillisAsState
import org.koin.androidx.compose.koinViewModel

@Composable
fun TenSecsBackButton(
    player: Player,
    modifier: Modifier = Modifier,
    viewModel: TrimmerViewModel = koinViewModel(),
) {
    val colors = LocalAppColors.current
    val startPos by viewModel.collectStartPosInMillisAsState()

    IconButton(
        onClick = { player.seekTenSecsBack(startPos) },
        modifier = modifier.size(40.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.previous),
            contentDescription = stringResource(id = R.string.ten_secs_back),
            tint = colors.primary,
            modifier = Modifier.fillMaxSize()
        )
    }
}