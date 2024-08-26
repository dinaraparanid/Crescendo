package com.paranid5.crescendo.trimmer.presentation.ui.effects

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
        modifier.clickableWithRipple(bounded = true) {
            onUiIntent(TrimmerUiIntent.ShowEffect(ShownEffects.PITCH_SPEED))
            coroutineScope.launch { effectsScaffoldState?.show() }
        },
    ) {
        PitchSpeedIcon(Modifier.align(Alignment.CenterHorizontally))
        PitchSpeedLabel(Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
private fun PitchSpeedIcon(modifier: Modifier = Modifier) =
    Icon(
        painter = painterResource(R.drawable.pitch_speed),
        contentDescription = labelMessage(),
        tint = colors.primary,
        modifier = modifier.size(dimensions.padding.big),
    )

@Composable
private fun PitchSpeedLabel(modifier: Modifier) =
    Text(
        text = labelMessage(),
        color = colors.primary,
        style = typography.captionSm,
        modifier = modifier,
    )

@Composable
private fun labelMessage() =
    "${stringResource(R.string.pitch)}/${stringResource(R.string.speed)}"
