package com.paranid5.crescendo.presentation.main.trimmer.views.playback

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.presentation.main.trimmer.player.TrackPlayer
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.effects.playback.CleanUpEffect
import com.paranid5.crescendo.presentation.main.trimmer.effects.playback.OutOfBordersEffect
import com.paranid5.crescendo.presentation.main.trimmer.effects.playback.PlayPauseEffect
import com.paranid5.crescendo.presentation.main.trimmer.effects.playback.PlaybackParamsEffect

@Composable
fun PlaybackButtons(
    viewModel: TrimmerViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val player by remember {
        lazy { TrackPlayer(context, viewModel) }
    }

    OutOfBordersEffect(viewModel)
    PlayPauseEffect(player, viewModel)
    CleanUpEffect(player, viewModel)
    PlaybackParamsEffect(player, viewModel)

    Row(modifier) {
        TenSecsBackButton(
            player = player,
            viewModel = viewModel,
            modifier = Modifier
                .padding(4.dp)
                .align(Alignment.CenterVertically)
        )

        Spacer(Modifier.width(16.dp))

        PlayPauseButton(
            viewModel = viewModel,
            modifier = Modifier
                .padding(4.dp)
                .align(Alignment.CenterVertically)
        )

        Spacer(Modifier.width(16.dp))

        TenSecsForwardButton(
            player = player,
            viewModel = viewModel,
            modifier = Modifier
                .padding(4.dp)
                .align(Alignment.CenterVertically)
        )
    }
}