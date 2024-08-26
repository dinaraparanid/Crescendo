package com.paranid5.crescendo.trimmer.presentation.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.trimmer.view_model.TrimmerState
import com.paranid5.crescendo.ui.foundation.AppRippleButton

@Composable
internal fun FileSaveButton(
    state: TrimmerState,
    isFileSaveDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
) {
    val trimRange = remember(state.playbackPositions.trimRange) {
        state.playbackPositions.trimRange
    }

    val trackDurationMillis = remember(state.trackDurationInMillis) {
        state.trackDurationInMillis
    }

    var isFileSaveDialogShown by isFileSaveDialogShownState

    val isClickable by remember(trimRange, trackDurationMillis) {
        derivedStateOf { trimRange.totalDurationMillis in 1..trackDurationMillis }
    }

    AppRippleButton(
        modifier = modifier,
        isEnabled = isClickable,
        shape = RoundedCornerShape(dimensions.corners.extraMedium),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.background.alternative,
        ),
        onClick = { isFileSaveDialogShown = true },
        content = { FileSaveButtonLabel(textModifier) },
    )
}

@Composable
private fun FileSaveButtonLabel(modifier: Modifier = Modifier) =
    Text(
        text = stringResource(R.string.save),
        color = colors.text.primary,
        style = typography.body,
        fontWeight = FontWeight.Bold,
        modifier = modifier,
    )
