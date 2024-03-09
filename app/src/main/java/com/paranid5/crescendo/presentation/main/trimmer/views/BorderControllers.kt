package com.paranid5.crescendo.presentation.main.trimmer.views

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
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.utils.extensions.timeStringMs
import com.paranid5.crescendo.utils.extensions.toTimeOrNull
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.properties.compose.collectEndPosInMillisAsState
import com.paranid5.crescendo.presentation.main.trimmer.properties.compose.collectStartPosInMillisAsState
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors
import com.paranid5.crescendo.presentation.ui.utils.DefaultOutlinedTextField

/** hh:mm:ss.xxx */
private const val CORRECT_TIME_INPUT_LENGTH = 2 + 1 + 2 + 1 + 2 + 1 + 3

@Composable
fun BorderControllers(modifier: Modifier = Modifier) =
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
    viewModel: TrimmerViewModel = koinActivityViewModel(),
) {
    val startMillis by viewModel.collectStartPosInMillisAsState()

    BorderController(
        label = stringResource(R.string.start_time),
        millis = startMillis,
        setMillis = viewModel::setStartPosInMillis,
        modifier = modifier
    )
}

@Composable
private fun EndController(
    modifier: Modifier = Modifier,
    viewModel: TrimmerViewModel = koinActivityViewModel(),
) {
    val endMillis by viewModel.collectEndPosInMillisAsState()

    BorderController(
        label = stringResource(R.string.end_time),
        millis = endMillis,
        setMillis = viewModel::setEndPosInMillis,
        modifier = modifier
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

    DefaultOutlinedTextField(
        value = millisStr,
        modifier = modifier,
        onValueChange = {
            millisStr = it.take(CORRECT_TIME_INPUT_LENGTH)
            it.toTimeOrNull()?.let(setMillis)
        },
        label = { BorderControllerLabel(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
    )
}

@Composable
private fun BorderControllerLabel(text: String, modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    Text(
        text = text,
        color = colors.primary,
        fontSize = 12.sp,
        modifier = modifier
    )
}