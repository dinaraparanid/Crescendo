package com.paranid5.crescendo.presentation.main.trimmer.views

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.R
import com.paranid5.crescendo.domain.caching.Formats
import com.paranid5.crescendo.domain.trimming.FadeDurations
import com.paranid5.crescendo.domain.trimming.TrimRange
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.properties.fadeDurationsFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.trimRangeFlow
import com.paranid5.crescendo.presentation.main.trimmer.properties.trackPathOrNullFlow
import com.paranid5.crescendo.presentation.main.trimmer.views.file_save_dialog.FileSaveDialogContent
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileSaveDialog(
    viewModel: TrimmerViewModel,
    isDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAppColors.current

    val trackPath by viewModel.trackPathOrNullFlow.collectAsState(initial = null)
    val trimRange by viewModel.trimRangeFlow.collectAsState(initial = TrimRange())
    val fadeDurations by viewModel.fadeDurationsFlow.collectAsState(initial = FadeDurations())

    var isDialogShown by isDialogShownState

    val filenameState = rememberInitialFilename(trackPath)
    val filename by filenameState

    val isSaveButtonClickable by remember(filename, trimRange) {
        derivedStateOf { filename.isNotEmpty() && trimRange.totalDurationSecs > 0 }
    }

    val fileSaveOptions = audioFileSaveOptions()

    val selectedSaveOptionIndexState = remember { mutableIntStateOf(0) }
    val selectedSaveOptionIndex by selectedSaveOptionIndexState

    val audioFormat by remember(selectedSaveOptionIndex) {
        derivedStateOf { Formats.entries[selectedSaveOptionIndex] }
    }

    if (isDialogShown)
        AlertDialog(onDismissRequest = { isDialogShown = false }) {
            Card(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.background)
            ) {
                FileSaveDialogContent(
                    isDialogShownState = isDialogShownState,
                    filenameState = filenameState,
                    fileSaveOptions = fileSaveOptions,
                    selectedSaveOptionIndexState = selectedSaveOptionIndexState,
                    trackPath = trackPath!!,
                    audioFormat = audioFormat,
                    trimRange = trimRange,
                    fadeDurations = fadeDurations,
                    isSaveButtonClickable = isSaveButtonClickable
                )
            }
        }
}

@Composable
private fun rememberInitialFilename(trackPath: String?): MutableState<String> {
    val initialFile by remember(trackPath) {
        derivedStateOf { trackPath?.let(::File) }
    }

    val initialFilename by remember(initialFile) {
        derivedStateOf { initialFile?.nameWithoutExtension ?: "" }
    }

    return remember(initialFilename) { mutableStateOf(initialFilename) }
}

@Composable
private fun audioFileSaveOptions() = arrayOf(
    stringResource(R.string.mp3),
    stringResource(R.string.wav),
    stringResource(R.string.aac),
)