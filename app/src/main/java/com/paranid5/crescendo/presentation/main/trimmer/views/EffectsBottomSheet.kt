package com.paranid5.crescendo.presentation.main.trimmer.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.presentation.main.trimmer.ShownEffects
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.views.effects.EffectsScreen
import com.paranid5.crescendo.presentation.ui.utils.PushUpButton

@Composable
fun EffectsBottomSheet(
    effects: ShownEffects,
    viewModel: TrimmerViewModel,
    alpha: Float,
    modifier: Modifier = Modifier
) = Box(modifier) {
    PushUpButton(
        alpha = alpha,
        modifier = Modifier
            .padding(top = 12.dp)
            .align(Alignment.TopCenter)
    )

    EffectsScreen(
        effects = effects,
        viewModel = viewModel,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp, start = 8.dp, end = 8.dp)
    )
}