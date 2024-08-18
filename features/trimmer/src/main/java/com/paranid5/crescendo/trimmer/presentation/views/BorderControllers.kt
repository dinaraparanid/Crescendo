package com.paranid5.crescendo.trimmer.presentation.views

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
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectEndPosInMillisAsState
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectStartPosInMillisAsState
import com.paranid5.crescendo.ui.foundation.AppOutlinedTextField
import com.paranid5.crescendo.utils.extensions.timeStringMs
import com.paranid5.crescendo.utils.extensions.toTimeOrNull
import org.koin.androidx.compose.koinViewModel

/** hh:mm:ss.xxx */
private const val CORRECT_TIME_INPUT_LENGTH = 2 + 1 + 2 + 1 + 2 + 1 + 3

@Composable
internal fun BorderControllers(modifier: Modifier = Modifier) =
    Row(modifier) {
        Spacer(Modifier.weight(1F))
        StartController()
        Spacer(Modifier.weight(1F))
        EndController()
        Spacer(Modifier.weight(1F))
    }

@Composable
private fun StartController(
    modifier: Modifier = Modifier,
    viewModel: TrimmerViewModel = koinViewModel(),
) {
    val startMillis by viewModel.collectStartPosInMillisAsState()

    BorderController(
        label = stringResource(R.string.start_time),
        millis = startMillis,
        setMillis = viewModel::setStartPosInMillis,
        modifier = modifier,
    )
}

@Composable
private fun EndController(
    modifier: Modifier = Modifier,
    viewModel: TrimmerViewModel = koinViewModel(),
) {
    val endMillis by viewModel.collectEndPosInMillisAsState()

    BorderController(
        label = stringResource(R.string.end_time),
        millis = endMillis,
        setMillis = viewModel::setEndPosInMillis,
        modifier = modifier,
    )
}

@Composable
private fun BorderController(
    label: String,
    millis: Long,
    setMillis: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var millisStr by remember(millis) {
        mutableStateOf(millis.timeStringMs)
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
        color = colors.primary,
        style = typography.caption,
        modifier = modifier,
    )