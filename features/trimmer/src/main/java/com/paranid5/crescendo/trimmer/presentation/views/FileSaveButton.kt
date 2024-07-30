package com.paranid5.crescendo.trimmer.presentation.views

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectTrackDurationInMillisAsState
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectTrimRangeAsState
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun FileSaveButton(
    isFileSaveDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    viewModel: TrimmerViewModel = koinViewModel(),
) {
    val trimRange by viewModel.collectTrimRangeAsState()
    val trackDurationMillis by viewModel.collectTrackDurationInMillisAsState()

    var isFileSaveDialogShown by isFileSaveDialogShownState

    val isClickable by remember(trimRange, trackDurationMillis) {
        derivedStateOf { trimRange.totalDurationMillis in 1..trackDurationMillis }
    }

    Button(
        modifier = modifier,
        enabled = isClickable,
        shape = RoundedCornerShape(dimensions.corners.extraMedium),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.background.alternative,
        ),
        onClick = { isFileSaveDialogShown = true },
        content = { FileSaveButtonLabel(textModifier) }
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