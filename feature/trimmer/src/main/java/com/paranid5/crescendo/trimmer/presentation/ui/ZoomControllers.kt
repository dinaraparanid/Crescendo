package com.paranid5.crescendo.trimmer.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.trimmer.presentation.WAVEFORM_SPIKE_WIDTH_RATIO
import com.paranid5.crescendo.trimmer.presentation.effects.waveform.InitZoomStepsEffect
import com.paranid5.crescendo.trimmer.view_model.TrimmerState
import com.paranid5.crescendo.trimmer.view_model.TrimmerUiIntent

private val ZoomButtonSize = 32.dp
private val ZoomIconSize = 16.dp

@Composable
internal fun ZoomControllers(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    screenWidthPx: Int,
    modifier: Modifier = Modifier,
    spikeWidthRatio: Int = WAVEFORM_SPIKE_WIDTH_RATIO,
) {
    InitZoomStepsEffect(
        state = state,
        onUiIntent = onUiIntent,
        screenWidthPx = screenWidthPx,
        spikeWidthRatio = spikeWidthRatio,
    )

    ZoomControllersContent(
        state = state,
        onUiIntent = onUiIntent,
        modifier = modifier
            .clip(RoundedCornerShape(dimensions.corners.extraMedium))
            .background(colors.background.highContrast),
    )
}

@Composable
private fun ZoomControllersContent(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = Row(modifier) {
    ZoomInButton(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier
            .align(Alignment.CenterVertically)
            .padding(horizontal = dimensions.padding.extraSmall),
    )

    ZoomRatioLabel(
        state = state,
        modifier = Modifier.align(Alignment.CenterVertically),
    )

    ZoomOutButton(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier
            .align(Alignment.CenterVertically)
            .padding(horizontal = dimensions.padding.extraSmall),
    )
}

@Composable
private fun ZoomInButton(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val canZoomIn = remember(state.waveformProperties.canZoomIn) {
        state.waveformProperties.canZoomIn
    }

    ZoomButton(
        modifier = modifier,
        enabled = canZoomIn,
        imageVector = ImageVector.vectorResource(R.drawable.ic_zoom_in),
        contentDescription = stringResource(R.string.zoom_in),
    ) {
        onUiIntent(TrimmerUiIntent.Waveform.ZoomIn)
    }
}

@Composable
private fun ZoomOutButton(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val canZoomOut = remember(state.waveformProperties.canZoomOut) {
        state.waveformProperties.canZoomOut
    }

    ZoomButton(
        modifier = modifier,
        enabled = canZoomOut,
        imageVector = ImageVector.vectorResource(R.drawable.ic_zoom_out),
        contentDescription = stringResource(R.string.zoom_out),
    ) {
        onUiIntent(TrimmerUiIntent.Waveform.ZoomOut)
    }
}

@Composable
private fun ZoomButton(
    imageVector: ImageVector,
    enabled: Boolean,
    contentDescription: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) = IconButton(
    onClick = onClick,
    enabled = enabled,
    modifier = modifier.size(ZoomButtonSize),
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = colors.text.onHighContrast,
        modifier = Modifier.size(ZoomIconSize),
    )
}

@Composable
private fun ZoomRatioLabel(
    state: TrimmerState,
    modifier: Modifier = Modifier,
) {
    val zoom = remember(state.waveformProperties.zoomLevel) {
        state.waveformProperties.zoomLevel
    }

    Text(
        text = "${1 shl zoom}X",
        color = colors.text.onHighContrast,
        style = typography.caption,
        modifier = modifier,
    )
}
