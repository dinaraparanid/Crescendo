package com.paranid5.crescendo.presentation.main.trimmer.views.effects

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
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.presentation.main.trimmer.composition_locals.LocalTrimmerEffectSheetState
import com.paranid5.crescendo.presentation.main.trimmer.entities.ShownEffects
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FadeButton(
    modifier: Modifier = Modifier,
    viewModel: TrimmerViewModel = koinActivityViewModel()
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
    val colors = LocalAppColors.current

    Icon(
        painter = painterResource(R.drawable.equalizer),
        contentDescription = stringResource(R.string.fade),
        tint = colors.primary,
        modifier = modifier.size(20.dp)
    )
}

@Composable
private fun EffectLabel(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    Text(
        text = stringResource(R.string.fade),
        color = colors.primary,
        fontSize = 8.sp,
        modifier = modifier
    )
}