package com.paranid5.crescendo.trimmer.presentation.views.effects

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.trimmer.domain.entities.ShownEffects
import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel
import com.paranid5.crescendo.trimmer.presentation.composition_locals.LocalTrimmerEffectSheetState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

private val FadeIconSize = 20.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun FadeButton(
    modifier: Modifier = Modifier,
    viewModel: TrimmerViewModel = koinViewModel()
) {
    val effectsScaffoldState = LocalTrimmerEffectSheetState.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier.clickable {
            viewModel.setShownEffectsOrd(ShownEffects.FADE.ordinal)
            coroutineScope.launch { effectsScaffoldState?.show() }
        }
    ) {
        FadeIcon(Modifier.align(Alignment.CenterHorizontally))
        EffectLabel(Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
private fun FadeIcon(modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(R.drawable.equalizer),
        contentDescription = stringResource(R.string.fade),
        tint = colors.primary,
        modifier = modifier.size(FadeIconSize),
    )
}

@Composable
private fun EffectLabel(modifier: Modifier = Modifier) =
    Text(
        text = stringResource(R.string.fade),
        color = colors.primary,
        style = typography.captionSm,
        modifier = modifier,
    )