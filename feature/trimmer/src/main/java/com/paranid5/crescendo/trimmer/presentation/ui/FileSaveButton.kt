package com.paranid5.crescendo.trimmer.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.trimmer.view_model.TrimmerState
import com.paranid5.crescendo.ui.foundation.AppRippleButton
import com.paranid5.crescendo.ui.foundation.AppText

@Composable
internal fun FileSaveButton(
    state: TrimmerState,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val trimRange = remember(state.playbackPositions.trimRange) {
        state.playbackPositions.trimRange
    }

    val trackDurationMillis = remember(state.trackDurationInMillis) {
        state.trackDurationInMillis
    }

    val isClickable by remember(trimRange, trackDurationMillis) {
        derivedStateOf { trimRange.totalDurationMillis in 1..trackDurationMillis }
    }

    AppRippleButton(
        modifier = modifier,
        isEnabled = isClickable,
        onClick = onClick,
        content = { FileSaveButtonLabel(textModifier) },
    )
}

@Composable
private fun FileSaveButtonLabel(modifier: Modifier = Modifier) =
    AppText(
        text = stringResource(R.string.save),
        modifier = modifier,
        style = typography.body.copy(
            color = colors.text.primary,
            fontWeight = FontWeight.W700,
        ),
    )
