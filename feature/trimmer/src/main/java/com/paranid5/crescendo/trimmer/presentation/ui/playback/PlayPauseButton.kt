package com.paranid5.crescendo.trimmer.presentation.ui.playback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import arrow.core.raise.nullable
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.trimmer.presentation.composition_locals.LocalTrimmerFocusPoints
import com.paranid5.crescendo.trimmer.view_model.TrimmerState
import com.paranid5.crescendo.trimmer.view_model.TrimmerUiIntent

@Composable
internal fun PlayPauseButton(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = nullable {
    val focusPoints = LocalTrimmerFocusPoints.current.bind()

    val isPlaying = remember(state.playbackProperties.isPlaying) {
        state.playbackProperties.isPlaying
    }

    IconButton(
        modifier = modifier.background(color = colors.secondary, shape = CircleShape),
        onClick = {
            onUiIntent(TrimmerUiIntent.Player.UpdatePlayingState)
            val nextPlayingState = isPlaying.not()
            if (nextPlayingState) focusPoints.playbackFocusRequester.requestFocus()
        },
    ) {
        when {
            isPlaying -> PauseIcon(
                Modifier
                    .fillMaxSize()
                    .padding(dimensions.padding.extraSmall),
            )

            else -> PlayIcon(
                Modifier
                    .fillMaxSize()
                    .padding(dimensions.padding.medium),
            )
        }
    }
}

@Composable
private fun PauseIcon(modifier: Modifier = Modifier) =
    Icon(
        imageVector = ImageVector.vectorResource(R.drawable.ic_pause),
        contentDescription = stringResource(R.string.pause),
        tint = colors.background.primary,
        modifier = modifier,
    )

@Composable
private fun PlayIcon(modifier: Modifier = Modifier) =
    Icon(
        imageVector = ImageVector.vectorResource(R.drawable.ic_play_filled),
        contentDescription = stringResource(R.string.play),
        tint = colors.background.primary,
        modifier = modifier,
    )
