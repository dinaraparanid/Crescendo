package com.paranid5.crescendo.trimmer.presentation.ui.effects

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.trimmer.domain.entities.ShownEffects
import com.paranid5.crescendo.trimmer.presentation.composition_locals.LocalTrimmerEffectSheetState
import com.paranid5.crescendo.trimmer.view_model.TrimmerUiIntent
import com.paranid5.crescendo.ui.utils.clickableWithRipple
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun PitchSpeedButton(
    onUiIntent: (TrimmerUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val effectsScaffoldState = LocalTrimmerEffectSheetState.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensions.padding.extraSmall),
        modifier = modifier
            .clip(RoundedCornerShape(dimensions.corners.extraSmall))
            .clickableWithRipple(bounded = true) {
                onUiIntent(TrimmerUiIntent.ShowEffect(ShownEffects.PITCH_SPEED))
                coroutineScope.launch { effectsScaffoldState?.show() }
            },
    ) {
        val commonModifier = Modifier.padding(horizontal = dimensions.padding.extraSmall)
        PitchSpeedIcon(commonModifier.padding(top = dimensions.padding.extraSmall))
        PitchSpeedLabel(commonModifier.padding(bottom = dimensions.padding.extraSmall))
    }
}

@Composable
private fun PitchSpeedIcon(modifier: Modifier = Modifier) =
    Icon(
        imageVector = ImageVector.vectorResource(R.drawable.ic_pitch_speed),
        contentDescription = labelMessage(),
        tint = colors.text.onHighContrast,
        modifier = modifier.size(dimensions.padding.big),
    )

@Composable
private fun PitchSpeedLabel(modifier: Modifier) =
    Text(
        text = labelMessage(),
        color = colors.text.onHighContrast,
        style = typography.captionSm,
        modifier = modifier,
    )

@Composable
private fun labelMessage() =
    "${stringResource(R.string.pitch)}/${stringResource(R.string.speed)}"
