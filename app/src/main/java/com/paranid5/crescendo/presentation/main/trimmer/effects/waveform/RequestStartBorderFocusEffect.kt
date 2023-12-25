package com.paranid5.crescendo.presentation.main.trimmer.effects.waveform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.paranid5.crescendo.presentation.composition_locals.trimmer.LocalTrimmerFocusPoints

@Composable
fun RequestStartBorderFocusEffect(
    isDraggedState: MutableState<Boolean>,
    isPositionedState: MutableState<Boolean>
) {
    val focusPoints = LocalTrimmerFocusPoints.current!!
    var isDragged by isDraggedState
    var isPositioned by isPositionedState

    LaunchedEffect(isDragged, isPositioned) {
        if (isDragged && isPositioned) {
            focusPoints
                .startBorderFocusRequester
                .requestFocus()

            isDragged = false
            isPositioned = false
        }
    }
}