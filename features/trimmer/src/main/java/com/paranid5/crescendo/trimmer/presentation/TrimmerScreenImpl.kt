package com.paranid5.crescendo.trimmer.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors
import com.paranid5.crescendo.trimmer.presentation.composition_locals.LocalTrimmerEffectSheetState
import com.paranid5.crescendo.trimmer.presentation.effects.PrepareForNewTrack
import com.paranid5.crescendo.trimmer.presentation.views.EffectsBottomSheet
import com.paranid5.crescendo.trimmer.presentation.views.TrimmerScreenContent

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun TrimmerScreenImpl(
    track: Track,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAppColors.current

    val effectsScaffoldState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    PrepareForNewTrack(track)

    ModalBottomSheetLayout(
        modifier = modifier,
        sheetBackgroundColor = Color.Transparent,
        sheetState = effectsScaffoldState,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContent = { EffectsBottomSheet(Modifier.background(colors.backgroundGradient)) },
    ) {
        CompositionLocalProvider(LocalTrimmerEffectSheetState provides effectsScaffoldState) {
            TrimmerScreenContent()
        }
    }
}