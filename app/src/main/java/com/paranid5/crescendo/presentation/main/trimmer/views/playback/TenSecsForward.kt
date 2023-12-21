package com.paranid5.crescendo.presentation.main.trimmer.views.playback

import android.media.MediaPlayer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.R
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.player.seekTenSecsForward
import com.paranid5.crescendo.presentation.main.trimmer.properties.durationInMillisFlow
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors

@Composable
fun TenSecsForwardButton(
    player: MediaPlayer,
    viewModel: TrimmerViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val durationInMillis by viewModel.durationInMillisFlow.collectAsState(initial = 0L)

    IconButton(
        onClick = { player.seekTenSecsForward(durationInMillis.toInt()) },
        modifier = modifier.size(40.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.next),
            contentDescription = stringResource(id = R.string.ten_secs_forward),
            tint = colors.primary,
            modifier = Modifier.fillMaxSize()
        )
    }
}