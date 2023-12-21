package com.paranid5.crescendo.presentation.main.trimmer.views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.R
import com.paranid5.crescendo.domain.utils.extensions.timeString
import com.paranid5.crescendo.domain.utils.extensions.toTimeOrNull
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.properties.endPosInMillisState
import com.paranid5.crescendo.presentation.main.trimmer.properties.setEndPosInMillis
import com.paranid5.crescendo.presentation.main.trimmer.properties.setStartPosInMillis
import com.paranid5.crescendo.presentation.main.trimmer.properties.startPosInMillisState
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import com.paranid5.crescendo.presentation.ui.utils.DefaultOutlinedTextField

@Composable
fun BorderControllers(
    viewModel: TrimmerViewModel,
    modifier: Modifier = Modifier
) = Row(modifier) {
    Spacer(Modifier.weight(1F))
    StartController(viewModel)
    Spacer(Modifier.weight(1F))
    EndController(viewModel)
    Spacer(Modifier.weight(1F))
}

@Composable
private fun StartController(
    viewModel: TrimmerViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current

    val startMillis by viewModel.startPosInMillisState.collectAsState()

    var startMillisStr by remember(startMillis) {
        mutableStateOf(startMillis.timeString)
    }

    DefaultOutlinedTextField(
        value = startMillisStr,
        modifier = modifier,
        onValueChange = {
            startMillisStr = it.take(8)
            it.toTimeOrNull()?.let(viewModel::setStartPosInMillis)
        },
        label = {
            Text(
                text = stringResource(R.string.start_time),
                color = colors.primary,
                fontSize = 12.sp,
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
    )
}

@Composable
private fun EndController(
    viewModel: TrimmerViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current

    val endMillis by viewModel.endPosInMillisState.collectAsState()

    var endMillisStr by remember(endMillis) {
        mutableStateOf(endMillis.timeString)
    }

    DefaultOutlinedTextField(
        value = endMillisStr,
        modifier = modifier,
        onValueChange = {
            endMillisStr = it.take(8)
            it.toTimeOrNull()?.let(viewModel::setEndPosInMillis)
        },
        label = {
            Text(
                text = stringResource(R.string.end_time),
                color = colors.primary,
                fontSize = 12.sp,
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
    )
}