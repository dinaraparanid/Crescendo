package com.paranid5.crescendo.presentation.main.trimmer.effects.waveform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.paranid5.crescendo.presentation.main.trimmer.composition_locals.LocalTrimmerFocusPoints

@Composable
fun RequestEndBorderFocusEffect(
    isDraggedState: MutableState<Boolean>,
    isPositionedState: MutableState<Boolean>
) {
    val focusPoints = LocalTrimmerFocusPoints.current!!
    var isDragged by isDraggedState
    var isPositioned by isPositionedState

    LaunchedEffect(isDragged, isPositioned) {
        if (isDragged && isPositioned) {
            focusPoints
                .endBorderFocusRequester
                .requestFocus()

            isDragged = false
            isPositioned = false
        }
    }
}