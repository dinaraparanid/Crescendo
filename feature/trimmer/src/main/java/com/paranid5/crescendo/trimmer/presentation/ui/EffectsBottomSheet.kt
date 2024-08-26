package com.paranid5.crescendo.trimmer.presentation.ui

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
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.trimmer.presentation.ui.effects.EffectsScreen
import com.paranid5.crescendo.trimmer.view_model.TrimmerState
import com.paranid5.crescendo.trimmer.view_model.TrimmerUiIntent
import com.paranid5.crescendo.ui.utils.PushUpButton
import com.paranid5.crescendo.utils.extensions.pxToDp

private val PushUpButtonTopPadding = 12.dp
private val EffectsScreenTopPadding = 24.dp
private val BottomPadding = 12.dp

@Composable
internal fun EffectsBottomSheet(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var buttonSizePx by remember { mutableIntStateOf(1) }
    var effectsSizePx by remember { mutableIntStateOf(1) }

    val requiredHeightPx by remember(buttonSizePx, effectsSizePx) {
        derivedStateOf { buttonSizePx + effectsSizePx }
    }

    val requiredHeight = requiredHeightPx.pxToDp()

    val maxHeight = remember(requiredHeight) {
        requiredHeight + PushUpButtonTopPadding + EffectsScreenTopPadding + BottomPadding
    }

    Box(modifier.heightIn(max = maxHeight)) {
        PushUpButton(
            alpha = 0F,
            modifier = Modifier
                .padding(top = PushUpButtonTopPadding)
                .align(Alignment.TopCenter)
                .onGloballyPositioned { buttonSizePx = it.size.height },
        )

        EffectsScreen(
            state = state,
            onUiIntent = onUiIntent,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = EffectsScreenTopPadding,
                    start = dimensions.padding.small,
                    end = dimensions.padding.small,
                )
                .onGloballyPositioned { effectsSizePx = it.size.height },
        )
    }
}