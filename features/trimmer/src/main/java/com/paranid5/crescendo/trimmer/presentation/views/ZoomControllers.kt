package com.paranid5.crescendo.trimmer.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel
import com.paranid5.crescendo.trimmer.presentation.WAVEFORM_SPIKE_WIDTH_RATIO
import com.paranid5.crescendo.trimmer.presentation.effects.waveform.InitZoomStepsEffect
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectCanZoomInAsState
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectCanZoomOutAsState
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectZoomAsState
import com.paranid5.crescendo.trimmer.presentation.properties.zoomIn
import com.paranid5.crescendo.trimmer.presentation.properties.zoomOut
import org.koin.androidx.compose.koinViewModel

private val ZoomButtonSize = 32.dp

@Composable
internal fun ZoomControllers(
    screenWidthPxState: MutableIntState,
    modifier: Modifier = Modifier,
    spikeWidthRatio: Int = WAVEFORM_SPIKE_WIDTH_RATIO,
) {
    InitZoomStepsEffect(
        screenWidthPxState = screenWidthPxState,
        spikeWidthRatio = spikeWidthRatio,
    )

    ZoomControllersContent(
        modifier
            .clip(RoundedCornerShape(dimensions.corners.extraMedium))
            .background(colors.background.alternative)
    )
}

@Composable
private fun ZoomControllersContent(modifier: Modifier = Modifier) =
    Row(modifier) {
        ZoomInButton(
            Modifier
                .align(Alignment.CenterVertically)
                .padding(horizontal = dimensions.padding.extraSmall)
        )

        ZoomRatioLabel(Modifier.align(Alignment.CenterVertically))

        ZoomOutButton(
            Modifier
                .align(Alignment.CenterVertically)
                .padding(horizontal = dimensions.padding.extraSmall)
        )
    }

@Composable
private fun ZoomInButton(
    modifier: Modifier = Modifier,
    viewModel: TrimmerViewModel = koinViewModel(),
) {
    val canZoomIn by viewModel.collectCanZoomInAsState()

    ZoomButton(
        modifier = modifier,
        enabled = canZoomIn,
        iconPainter = painterResource(R.drawable.zoom_in),
        contentDescription = stringResource(R.string.zoom_in),
        onClick = viewModel::zoomIn,
    )
}

@Composable
private fun ZoomOutButton(
    modifier: Modifier = Modifier,
    viewModel: TrimmerViewModel = koinViewModel(),
) {
    val canZoomOut by viewModel.collectCanZoomOutAsState()

    ZoomButton(
        modifier = modifier,
        enabled = canZoomOut,
        iconPainter = painterResource(R.drawable.zoom_out),
        contentDescription = stringResource(R.string.zoom_out),
        onClick = viewModel::zoomOut,
    )
}

@Composable
private fun ZoomButton(
    iconPainter: Painter,
    enabled: Boolean,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) = IconButton(
    onClick = onClick,
    enabled = enabled,
    modifier = modifier.size(ZoomButtonSize),
) {
    Icon(
        painter = iconPainter,
        contentDescription = contentDescription,
        tint = colors.primary,
        modifier = Modifier.size(dimensions.padding.extraMedium),
    )
}

@Composable
private fun ZoomRatioLabel(
    modifier: Modifier = Modifier,
    viewModel: TrimmerViewModel = koinViewModel(),
) {
    val zoom by viewModel.collectZoomAsState()

    Text(
        text = "${1 shl zoom}X",
        color = colors.primary,
        style = typography.caption,
        modifier = modifier,
    )
}