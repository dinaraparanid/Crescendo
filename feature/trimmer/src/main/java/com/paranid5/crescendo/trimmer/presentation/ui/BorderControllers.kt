package com.paranid5.crescendo.trimmer.presentation.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.trimmer.view_model.TrimmerState
import com.paranid5.crescendo.trimmer.view_model.TrimmerUiIntent
import com.paranid5.crescendo.ui.foundation.AppOutlinedTextField
import com.paranid5.crescendo.utils.extensions.timeFormatMs
import com.paranid5.crescendo.utils.extensions.toTimeOrNull

/** hh:mm:ss.xxx */
private const val CORRECT_TIME_INPUT_LENGTH = 2 + 1 + 2 + 1 + 2 + 1 + 3

@Composable
internal fun BorderControllers(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = Row(modifier) {
    Spacer(Modifier.weight(1F))

    StartController(
        state = state,
        onUiIntent = onUiIntent,
    )

    Spacer(Modifier.weight(1F))

    EndController(
        state = state,
        onUiIntent = onUiIntent,
    )

    Spacer(Modifier.weight(1F))
}

@Composable
private fun StartController(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val startMillis = remember(state.playbackPositions.startPosInMillis) {
        state.playbackPositions.startPosInMillis
    }

    BorderController(
        label = stringResource(R.string.start_time),
        millis = startMillis,
        setMillis = {
            onUiIntent(TrimmerUiIntent.Positions.UpdateStartPosition(startPositionInMillis = it))
        },
        modifier = modifier,
    )
}

@Composable
private fun EndController(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val endMillis = remember(state.playbackPositions.endPosInMillis) {
        state.playbackPositions.endPosInMillis
    }

    BorderController(
        label = stringResource(R.string.end_time),
        millis = endMillis,
        setMillis = {
            onUiIntent(TrimmerUiIntent.Positions.UpdateEndPosition(endPositionInMillis = it))
        },
        modifier = modifier,
    )
}

@Composable
private fun BorderController(
    label: String,
    millis: Long,
    setMillis: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var millisStr by remember(millis) {
        mutableStateOf(millis.timeFormatMs)
    }

    AppOutlinedTextField(
        value = millisStr,
        modifier = modifier,
        label = { BorderControllerLabel(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
    ) {
        millisStr = it.take(CORRECT_TIME_INPUT_LENGTH)
        it.toTimeOrNull()?.let(setMillis)
    }
}

@Composable
private fun BorderControllerLabel(text: String, modifier: Modifier = Modifier) =
    Text(
        text = text,
        style = typography.caption,
        modifier = modifier,
    )
