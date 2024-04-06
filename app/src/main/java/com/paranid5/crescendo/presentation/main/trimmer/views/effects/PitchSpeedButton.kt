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
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.composition_locals.LocalTrimmerEffectSheetState
import com.paranid5.crescendo.presentation.main.trimmer.entities.ShownEffects
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PitchSpeedButton(
    modifier: Modifier = Modifier,
    viewModel: TrimmerViewModel = koinViewModel()
) {
    val effectsScaffoldState = LocalTrimmerEffectSheetState.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier.clickable {
            viewModel.setShownEffectsOrd(ShownEffects.PITCH_SPEED.ordinal)
            coroutineScope.launch { effectsScaffoldState?.show() }
        }
    ) {
        PitchSpeedIcon(Modifier.align(Alignment.CenterHorizontally))
        PitchSpeedLabel(Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
private fun PitchSpeedIcon(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    Icon(
        painter = painterResource(R.drawable.pitch_speed),
        contentDescription = labelMessage(),
        tint = colors.primary,
        modifier = modifier.size(20.dp)
    )
}

@Composable
private fun PitchSpeedLabel(modifier: Modifier) {
    val colors = LocalAppColors.current

    Text(
        text = labelMessage(),
        color = colors.primary,
        fontSize = 8.sp,
        modifier = modifier
    )
}

@Composable
private fun labelMessage() =
    "${stringResource(R.string.pitch)}/${stringResource(R.string.speed)}"