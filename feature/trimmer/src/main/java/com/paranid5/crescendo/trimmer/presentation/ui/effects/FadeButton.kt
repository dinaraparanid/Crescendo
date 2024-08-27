package com.paranid5.crescendo.trimmer.presentation.ui.effects

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
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.trimmer.domain.entities.ShownEffects
import com.paranid5.crescendo.trimmer.presentation.composition_locals.LocalTrimmerEffectSheetState
import com.paranid5.crescendo.trimmer.view_model.TrimmerUiIntent
import com.paranid5.crescendo.ui.utils.clickableWithRipple
import kotlinx.coroutines.launch

private val FadeIconSize = 20.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun FadeButton(
    onUiIntent: (TrimmerUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val effectsScaffoldState = LocalTrimmerEffectSheetState.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(dimensions.corners.extraSmall))
            .clickableWithRipple(bounded = true) {
                onUiIntent(TrimmerUiIntent.ShowEffect(ShownEffects.FADE))
                coroutineScope.launch { effectsScaffoldState?.show() }
            },
    ) {
        val commonModifier = Modifier.padding(horizontal = dimensions.padding.extraSmall)
        FadeIcon(commonModifier.padding(top = dimensions.padding.extraSmall))
        EffectLabel(commonModifier.padding(bottom = dimensions.padding.extraSmall))
    }
}

@Composable
private fun FadeIcon(modifier: Modifier = Modifier) =
    Icon(
        imageVector = ImageVector.vectorResource(R.drawable.ic_equalizer),
        contentDescription = stringResource(R.string.fade),
        tint = colors.text.onHighContrast,
        modifier = modifier.size(FadeIconSize),
    )

@Composable
private fun EffectLabel(modifier: Modifier = Modifier) =
    Text(
        text = stringResource(R.string.fade),
        color = colors.text.onHighContrast,
        style = typography.captionSm,
        modifier = modifier,
    )