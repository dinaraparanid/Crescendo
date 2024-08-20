package com.paranid5.crescendo.trimmer.presentation.views.playback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.trimmer.domain.entities.FocusPoints
import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel
import com.paranid5.crescendo.trimmer.presentation.composition_locals.LocalTrimmerFocusPoints
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectIsPlayingAsState
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun PlayPauseButton(
    modifier: Modifier = Modifier,
    viewModel: TrimmerViewModel = koinViewModel(),
) {
    val focusPoints = LocalTrimmerFocusPoints.current!!
    val isPlaying by viewModel.collectIsPlayingAsState()

    IconButton(
        onClick = { onClick(isPlaying, viewModel, focusPoints) },
        modifier = modifier.background(color = colors.secondary, shape = CircleShape)
    ) {
        when {
            isPlaying -> PauseIcon(
                Modifier
                    .fillMaxSize()
                    .padding(dimensions.padding.extraSmall)
            )

            else -> PlayIcon(
                Modifier
                    .fillMaxSize()
                    .padding(dimensions.padding.medium)
            )
        }
    }
}

@Composable
private fun PauseIcon(modifier: Modifier = Modifier) =
    Icon(
        painter = painterResource(id = R.drawable.ic_pause),
        contentDescription = stringResource(id = R.string.pause),
        tint = colors.background.primary,
        modifier = modifier,
    )

@Composable
private fun PlayIcon(modifier: Modifier = Modifier) =
    Icon(
        painter = painterResource(id = R.drawable.ic_play_filled),
        contentDescription = stringResource(id = R.string.play),
        tint = colors.background.primary,
        modifier = modifier,
    )

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