package com.paranid5.crescendo.presentation.main.trimmer.views

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.R
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.WAVEFORM_SPIKE_WIDTH_RATIO
import com.paranid5.crescendo.presentation.main.trimmer.effects.waveform.InitZoomStepsEffect
import com.paranid5.crescendo.presentation.main.trimmer.properties.canZoomInFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.canZoomOutFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.zoomIn
import com.paranid5.crescendo.presentation.main.trimmer.properties.zoomOut
import com.paranid5.crescendo.presentation.main.trimmer.properties.zoomState
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors

@Composable
fun ZoomControllers(
    viewModel: TrimmerViewModel,
    screenWidthPxState: MutableIntState,
    modifier: Modifier = Modifier,
    spikeWidthRatio: Int = WAVEFORM_SPIKE_WIDTH_RATIO,
) {
    val colors = LocalAppColors.current

    InitZoomStepsEffect(
        viewModel = viewModel,
        screenWidthPxState = screenWidthPxState,
        spikeWidthRatio = spikeWidthRatio
    )

    ZoomControllersContent(
        viewModel = viewModel,
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(colors.backgroundAlternative)
    )
}

@Composable
private fun ZoomControllersContent(viewModel: TrimmerViewModel, modifier: Modifier) =
    Row(modifier) {
        ZoomInButton(
            viewModel = viewModel,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(horizontal = 4.dp)
        )

        ZoomRatioLabel(
            viewModel = viewModel,
            modifier = Modifier.align(Alignment.CenterVertically)
        )

        ZoomOutButton(
            viewModel = viewModel,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(horizontal = 4.dp)
        )
    }

@Composable
private fun ZoomInButton(viewModel: TrimmerViewModel, modifier: Modifier = Modifier) {
    val canZoomIn by viewModel.canZoomInFlow.collectAsState(initial = true)

    ZoomButton(
        modifier = modifier,
        enabled = canZoomIn,
        iconPainter = painterResource(R.drawable.zoom_in),
        contentDescription = stringResource(R.string.zoom_in),
        onClick = viewModel::zoomIn,
    )
}

@Composable
private fun ZoomOutButton(viewModel: TrimmerViewModel, modifier: Modifier = Modifier) {
    val canZoomOut by viewModel.canZoomOutFlow.collectAsState(initial = false)

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
) {
    val colors = LocalAppColors.current

    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.size(32.dp)
    ) {
        Icon(
            painter = iconPainter,
            contentDescription = contentDescription,
            tint = colors.primary,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun ZoomRatioLabel(
    viewModel: TrimmerViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val zoom by viewModel.zoomState.collectAsState()

    Text(
        text = "${1 shl zoom}X",
        color = colors.primary,
        fontSize = 12.sp,
        modifier = modifier
    )
}