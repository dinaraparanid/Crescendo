package com.paranid5.crescendo.trimmer.presentation.views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel
import com.paranid5.crescendo.trimmer.presentation.effects.playback.CleanUpEffect
import com.paranid5.crescendo.trimmer.presentation.effects.playback.OutOfBordersEffect
import com.paranid5.crescendo.trimmer.presentation.effects.playback.PlayPauseEffect
import com.paranid5.crescendo.trimmer.presentation.effects.playback.PlaybackParamsEffect
import com.paranid5.crescendo.trimmer.presentation.effects.playback.PlaybackPositionTappedEffect
import com.paranid5.crescendo.trimmer.domain.player.TrackPlayer
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectTrackAsState
import com.paranid5.crescendo.trimmer.presentation.views.playback.PlayPauseButton
import com.paranid5.crescendo.trimmer.presentation.views.playback.TenSecsBackButton
import com.paranid5.crescendo.trimmer.presentation.views.playback.TenSecsForwardButton
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun PlaybackButtons(
    modifier: Modifier = Modifier,
    viewModel: TrimmerViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val track by viewModel.collectTrackAsState()

    val player by remember(track) {
        lazy { TrackPlayer(context, viewModel) }
    }

    OutOfBordersEffect()
    PlayPauseEffect(player ?: return)
    CleanUpEffect(player ?: return)
    PlaybackParamsEffect(player ?: return)
    PlaybackPositionTappedEffect(player ?: return)

    PlaybackButtonsContent(
        player = player ?: return,
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