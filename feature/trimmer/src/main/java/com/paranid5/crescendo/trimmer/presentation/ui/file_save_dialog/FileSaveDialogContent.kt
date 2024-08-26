package com.paranid5.crescendo.trimmer.presentation.ui.file_save_dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.common.caching.Formats
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.trimmer.view_model.TrimmerUiIntent
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun FileSaveDialogContent(
    onUiIntent: (TrimmerUiIntent) -> Unit,
    fileSaveOptions: ImmutableList<String>,
    audioFormat: Formats,
    isSaveButtonClickable: Boolean,
    isDialogShownState: MutableState<Boolean>,
    filenameState: MutableState<String>,
    selectedSaveOptionIndexState: MutableState<Int>,
    modifier: Modifier = Modifier
) {
    val outputFilename by filenameState

    Column(modifier) {
        Title(Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(dimensions.padding.medium))

        FilenameInput(filenameState = filenameState)
        Spacer(Modifier.height(dimensions.padding.medium))

        FileSaveOptionsMenu(
            fileSaveOptions = fileSaveOptions,
            selectedSaveOptionIndexState = selectedSaveOptionIndexState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensions.padding.medium),
        )

        Spacer(Modifier.height(dimensions.padding.medium))

        ConfirmButton(
            onUiIntent = onUiIntent,
            isSaveButtonClickable = isSaveButtonClickable,
            outputFilename = outputFilename,
            audioFormat = audioFormat,
            isDialogShownState = isDialogShownState,
            modifier = Modifier
                .padding(vertical = dimensions.padding.medium)
                .align(Alignment.CenterHorizontally),
        )
    }
}