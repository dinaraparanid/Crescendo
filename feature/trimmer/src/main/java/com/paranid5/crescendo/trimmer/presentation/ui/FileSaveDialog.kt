package com.paranid5.crescendo.trimmer.presentation.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.core.common.caching.Formats
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.trimmer.presentation.ui.file_save_dialog.FileSaveDialogContent
import com.paranid5.crescendo.trimmer.view_model.TrimmerState
import com.paranid5.crescendo.trimmer.view_model.TrimmerUiIntent
import com.paranid5.crescendo.ui.foundation.getOrNull
import kotlinx.collections.immutable.persistentListOf
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FileSaveDialog(
    state: TrimmerState,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    isDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
) {
    // TODO: убрать все стейты на уровне компоуза ниже

    val track = remember(state.trackState.getOrNull()) {
        state.trackState.getOrNull()
    }

    var isDialogShown by isDialogShownState

    val filenameState = rememberInitialFilename(track?.path)
    val filename by filenameState

    val isDialogSaveButtonClickable by remember(filename) {
        derivedStateOf(filename::isNotBlank)
    }

    val fileSaveOptions = audioFileSaveOptions()

    val selectedSaveOptionIndexState = remember { mutableIntStateOf(0) }
    val selectedSaveOptionIndex by selectedSaveOptionIndexState

    val audioFormat by remember(selectedSaveOptionIndex) {
        derivedStateOf { Formats.entries[selectedSaveOptionIndex] }
    }

    if (isDialogShown)
        BasicAlertDialog(
            modifier = modifier,
            onDismissRequest = { isDialogShown = false },
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(dimensions.corners.medium),
                colors = CardDefaults.cardColors(containerColor = colors.background.primary),
            ) {
                FileSaveDialogContent(
                    onUiIntent = onUiIntent,
                    isDialogShownState = isDialogShownState,
                    filenameState = filenameState,
                    fileSaveOptions = fileSaveOptions,
                    selectedSaveOptionIndexState = selectedSaveOptionIndexState,
                    audioFormat = audioFormat,
                    isSaveButtonClickable = isDialogSaveButtonClickable,
                )
            }
        }
}

@Composable
private fun rememberInitialFilename(trackPath: String?): MutableState<String> {
    val initialFile = remember(trackPath) {
        trackPath?.let(::File)
    }

    val initialFilename by remember(initialFile) {
        derivedStateOf { initialFile?.nameWithoutExtension.orEmpty() }
    }

    return remember(initialFilename) { mutableStateOf(initialFilename) }
}

@Composable
private fun audioFileSaveOptions() = persistentListOf(
    stringResource(R.string.mp3),
    stringResource(R.string.wav),
    stringResource(R.string.aac),
)
