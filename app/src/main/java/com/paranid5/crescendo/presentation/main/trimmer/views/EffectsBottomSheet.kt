package com.paranid5.crescendo.presentation.main.trimmer.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.properties.compose.collectShownEffectsAsState
import com.paranid5.crescendo.presentation.main.trimmer.views.effects.EffectsScreen
import com.paranid5.crescendo.presentation.ui.extensions.pxToDp
import com.paranid5.crescendo.presentation.ui.utils.PushUpButton

@Composable
fun EffectsBottomSheet(
    modifier: Modifier = Modifier,
    viewModel: TrimmerViewModel = koinActivityViewModel()
) {
    val effects by viewModel.collectShownEffectsAsState()
    var buttonSizePx by remember { mutableIntStateOf(1) }
    var effectsSizePx by remember { mutableIntStateOf(1) }

    val maxHeightPx by remember(buttonSizePx, effectsSizePx) {
        derivedStateOf { buttonSizePx + effectsSizePx }
    }

    val maxHeight = maxHeightPx.pxToDp() + 12.dp + 24.dp + 10.dp

    Box(modifier.heightIn(max = maxHeight)) {
        PushUpButton(
            alpha = 0F,
            modifier = Modifier
                .padding(top = 12.dp)
                .align(Alignment.TopCenter)
                .onGloballyPositioned {
                    buttonSizePx = it.size.height
                }
        )

        EffectsScreen(
            effects = effects,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 8.dp, end = 8.dp)
                .onGloballyPositioned {
                    effectsSizePx = it.size.height
                }
        )
    }
}