package com.paranid5.crescendo.feature.meta_editor.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.feature.meta_editor.view_model.MetaEditorState
import com.paranid5.crescendo.feature.meta_editor.view_model.MetaEditorUiIntent
import com.paranid5.crescendo.ui.foundation.isEvaluating
import com.paranid5.crescendo.ui.foundation.isOk
import com.paranid5.crescendo.ui.utils.clickableWithRipple

@Composable
internal fun Toolbar(
    state: MetaEditorState,
    onUiIntent: (MetaEditorUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(dimensions.padding.medium),
) {
    BackButton { onUiIntent(MetaEditorUiIntent.General.Back) }

    Spacer(Modifier.weight(1F))

    AnimatedVisibility(visible = state.trackPathUiState.isEvaluating.not()) {
        RefreshButton { onUiIntent(MetaEditorUiIntent.Lifecycle.Refresh) }
    }

    AnimatedVisibility(visible = state.trackPathUiState.isOk) {
        ApplyButton { onUiIntent(MetaEditorUiIntent.Lifecycle.Refresh) }
    }
}

@Composable
private fun BackButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) = Icon(
    imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_back),
    contentDescription = stringResource(R.string.back),
    tint = colors.icon.selected,
    modifier = modifier.clickableWithRipple(onClick = onClick),
)

@Composable
private fun RefreshButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    var angle by remember { mutableFloatStateOf(0F) }
    val animatedAngle by animateFloatAsState(angle, label = "rotation angle")

    Icon(
        imageVector = ImageVector.vectorResource(R.drawable.ic_refresh),
        contentDescription = stringResource(R.string.back),
        tint = colors.icon.selected,
        modifier = modifier
            .rotate(animatedAngle)
            .clickableWithRipple {
                angle = (angle + 360F) % 720F
                onClick()
            },
    )
}

@Composable
private fun ApplyButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) = Icon(
    imageVector = ImageVector.vectorResource(R.drawable.ic_check),
    contentDescription = stringResource(R.string.apply),
    tint = colors.icon.selected,
    modifier = modifier.clickableWithRipple(onClick = onClick),
)
