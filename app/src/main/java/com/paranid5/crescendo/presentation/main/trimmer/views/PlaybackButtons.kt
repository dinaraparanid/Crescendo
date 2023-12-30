package com.paranid5.crescendo.presentation.main.trimmer.views

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
import androidx.media3.common.Player
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.effects.playback.CleanUpEffect
import com.paranid5.crescendo.presentation.main.trimmer.effects.playback.OutOfBordersEffect
import com.paranid5.crescendo.presentation.main.trimmer.effects.playback.PlayPauseEffect
import com.paranid5.crescendo.presentation.main.trimmer.effects.playback.PlaybackParamsEffect
import com.paranid5.crescendo.presentation.main.trimmer.effects.playback.PlaybackPositionTappedEffect
import com.paranid5.crescendo.presentation.main.trimmer.player.TrackPlayer
import com.paranid5.crescendo.presentation.main.trimmer.views.playback.PlayPauseButton
import com.paranid5.crescendo.presentation.main.trimmer.views.playback.TenSecsBackButton
import com.paranid5.crescendo.presentation.main.trimmer.views.playback.TenSecsForwardButton

@Composable
fun PlaybackButtons(
    modifier: Modifier = Modifier,
    viewModel: TrimmerViewModel = koinActivityViewModel(),
) {
    val context = LocalContext.current

    val player by remember {
        lazy { TrackPlayer(context, viewModel) }
    }

    OutOfBordersEffect()
    PlayPauseEffect(player)
    CleanUpEffect(player)
    PlaybackParamsEffect(player)
    PlaybackPositionTappedEffect(player)

    PlaybackButtonsContent(
        player = player,
        modifier = modifier
    )
}

@Composable
private fun PlaybackButtonsContent(
    player: Player,
    modifier: Modifier = Modifier
) = Row(modifier) {
    TenSecsBackButton(
        player = player,
        modifier = Modifier
            .padding(4.dp)
            .align(Alignment.CenterVertically)
    )

    Spacer(Modifier.width(16.dp))

    PlayPauseButton(
        Modifier
            .padding(4.dp)
            .align(Alignment.CenterVertically)
    )

    Spacer(Modifier.width(16.dp))

    TenSecsForwardButton(
        player = player,
        modifier = Modifier
            .padding(4.dp)
            .align(Alignment.CenterVertically)
    )
}