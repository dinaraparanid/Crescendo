package com.paranid5.crescendo.presentation.main.trimmer.views.playback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.paranid5.crescendo.presentation.composition_locals.LocalTrimmerFocusPoints
import com.paranid5.crescendo.presentation.main.trimmer.FocusPoints
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.properties.isPlayingState
import com.paranid5.crescendo.presentation.main.trimmer.properties.setPlaying
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors

@Composable
fun PlayPauseButton(
    viewModel: TrimmerViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val focusPoints = LocalTrimmerFocusPoints.current!!
    val isPlaying by viewModel.isPlayingState.collectAsState()

    IconButton(
        onClick = { onClick(isPlaying, viewModel, focusPoints) },
        modifier = modifier
            .size(48.dp)
            .background(color = colors.secondary, shape = CircleShape)
    ) {
        when {
            isPlaying -> PlayIcon(Modifier.fillMaxSize())
            else -> PauseIcon(Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun PlayIcon(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    Icon(
        painter = painterResource(id = R.drawable.pause),
        contentDescription = stringResource(id = R.string.pause),
        tint = colors.background,
        modifier = modifier
    )
}

@Composable
private fun PauseIcon(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    Icon(
        painter = painterResource(id = R.drawable.play),
        contentDescription = stringResource(id = R.string.play),
        tint = colors.background,
        modifier = modifier
    )
}

private fun onClick(
    isPlaying: Boolean,
    viewModel: TrimmerViewModel,
    focusPoints: FocusPoints
) {
    val newPlaying = !isPlaying
    viewModel.setPlaying(newPlaying)

    if (newPlaying)
        focusPoints
            .playbackFocusRequester
            .requestFocus()
}