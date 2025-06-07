package com.paranid5.crescendo.trimmer.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.trimmer.presentation.composition_locals.LocalTrimmerEffectSheetState
import com.paranid5.crescendo.trimmer.presentation.ui.EffectsBottomSheet
import com.paranid5.crescendo.trimmer.presentation.ui.TrimmerScreenContent
import com.paranid5.crescendo.trimmer.view_model.TrimmerState
import com.paranid5.crescendo.trimmer.view_model.TrimmerUiIntent

@Composable
internal fun TrimmerScreenImpl(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val effectsScaffoldState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )

    ModalBottomSheetLayout(
        modifier = modifier,
        sheetBackgroundColor = Color.Transparent,
        sheetState = effectsScaffoldState,
        sheetShape = RoundedCornerShape(
            topStart = dimensions.padding.extraBig,
            topEnd = dimensions.padding.extraBig,
        ),
        sheetContent = {
            EffectsBottomSheet(
                state = state,
                onUiIntent = onUiIntent,
                modifier = Modifier.background(colors.background.gradient),
            )
        },
    ) {
        CompositionLocalProvider(LocalTrimmerEffectSheetState provides effectsScaffoldState) {
            TrimmerScreenContent(
                state = state,
                onUiIntent = onUiIntent,
            )
        }
    }
}