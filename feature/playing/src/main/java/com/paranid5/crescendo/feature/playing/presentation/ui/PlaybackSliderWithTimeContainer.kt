package com.paranid5.crescendo.feature.playing.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.paranid5.crescendo.core.common.PlaybackStatus
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.feature.playing.view_model.PlayingState
import com.paranid5.crescendo.ui.foundation.AppText
import com.paranid5.crescendo.utils.extensions.timeFormat

@Composable
internal fun PlaybackSliderWithTimeContainer(
    screenPlaybackStatus: PlaybackStatus,
    state: PlayingState,
    seekTo: (position: Long) -> Unit,
    modifier: Modifier = Modifier,
    onLiveSeekerClick: () -> Unit,
) = PlaybackSlider(
    state = state,
    seekTo = seekTo,
    modifier = modifier
) { curPosition, videoLength, color ->
    when {
        screenPlaybackStatus == PlaybackStatus.STREAMING && state.isLiveStreaming ->
            Box(Modifier.fillMaxWidth()) {
                LiveSeeker(
                    color = color,
                    modifier = Modifier.align(Alignment.Center),
                    onClick = onLiveSeekerClick,
                )
            }

        else -> {
            TimeText(curPosition, color)
            Spacer(Modifier.weight(1F))
            TimeText(videoLength, color)
        }
    }
}

@Composable
internal fun TimeText(time: Long, color: Color, modifier: Modifier = Modifier) =
    AppText(
        text = time.timeFormat,
        modifier = modifier,
        style = typography.body.copy(
            color = color,
        ),
    )

@Composable
internal fun LiveSeeker(
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) = Button(
    modifier = modifier,
    colors = ButtonDefaults.buttonColors(containerColor = color),
    shape = RoundedCornerShape(dimensions.corners.medium),
    content = { LiveLabel() },
    onClick = onClick,
)

@Composable
private fun LiveLabel(modifier: Modifier = Modifier) =
    AppText(
        text = stringResource(R.string.live),
        modifier = modifier,
        style = typography.regular.copy(
            color = colors.primary,
            fontWeight = FontWeight.W700,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Center,
        ),
    )
