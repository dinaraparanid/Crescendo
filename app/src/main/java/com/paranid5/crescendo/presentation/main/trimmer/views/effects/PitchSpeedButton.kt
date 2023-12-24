package com.paranid5.crescendo.presentation.main.trimmer.views.effects

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.R
import com.paranid5.crescendo.presentation.composition_locals.LocalTrimmerEffectSheetState
import com.paranid5.crescendo.presentation.main.trimmer.ShownEffects
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PitchSpeedButton(
    shownEffectsState: MutableIntState,
    modifier: Modifier = Modifier
) {
    val effectsScaffoldState = LocalTrimmerEffectSheetState.current
    var shownEffectsOrd by shownEffectsState
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier.clickable {
            shownEffectsOrd = ShownEffects.PITCH_SPEED.ordinal
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