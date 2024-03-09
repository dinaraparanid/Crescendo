package com.paranid5.crescendo.presentation.main.trimmer

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.impl.presentation.composition_locals.trimmer.LocalTrimmerEffectSheetState
import com.paranid5.crescendo.presentation.main.trimmer.views.EffectsBottomSheet
import com.paranid5.crescendo.presentation.main.trimmer.views.TrimmerScreenContent
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors

internal const val MIN_SPIKE_HEIGHT = 1F
internal const val DEFAULT_GRAPHICS_LAYER_ALPHA = 0.99F

internal const val CONTROLLER_RECT_WIDTH = 15F
internal const val CONTROLLER_RECT_OFFSET = 7F

internal const val CONTROLLER_CIRCLE_RADIUS = 25F
internal const val CONTROLLER_CIRCLE_CENTER = 16F
internal const val CONTROLLER_HEIGHT_OFFSET = CONTROLLER_CIRCLE_RADIUS + CONTROLLER_CIRCLE_CENTER

internal const val CONTROLLER_ARROW_CORNER_BACK_OFFSET = 8F
internal const val CONTROLLER_ARROW_CORNER_FRONT_OFFSET = 10F
internal const val CONTROLLER_ARROW_CORNER_OFFSET = 12F

internal const val PLAYBACK_RECT_WIDTH = 5F
internal const val PLAYBACK_RECT_OFFSET = 2F

internal const val PLAYBACK_CIRCLE_RADIUS = 12F
internal const val PLAYBACK_CIRCLE_CENTER = 8F

internal const val WAVEFORM_SPIKE_WIDTH_RATIO = 5

internal const val WAVEFORM_PADDING =
    (CONTROLLER_CIRCLE_RADIUS +
            CONTROLLER_CIRCLE_CENTER / 2 +
            CONTROLLER_RECT_WIDTH).toInt()

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TrimmerScreen(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    val effectsScaffoldState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
    )

    ModalBottomSheetLayout(
        modifier = modifier,
        sheetState = effectsScaffoldState,
        sheetBackgroundColor = colors.background,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetContent = { EffectsBottomSheet() },
    ) {
        CompositionLocalProvider(LocalTrimmerEffectSheetState provides effectsScaffoldState) {
            TrimmerScreenContent(Modifier.padding(top = 16.dp, start = 8.dp, end = 8.dp))
        }
    }
}