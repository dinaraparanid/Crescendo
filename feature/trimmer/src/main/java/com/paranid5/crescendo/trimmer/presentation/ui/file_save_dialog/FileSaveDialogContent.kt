package com.paranid5.crescendo.trimmer.presentation.ui.file_save_dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.trimmer.view_model.TrimmerState.FileSaveDialogProperties
import com.paranid5.crescendo.trimmer.view_model.TrimmerUiIntent
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun FileSaveDialogContent(
    state: FileSaveDialogProperties,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    fileSaveOptions: ImmutableList<String>,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Spacer(Modifier.height(dimensions.padding.extraMedium))

        FileSaveDialogLabel(Modifier.align(Alignment.CenterHorizontally))

        Spacer(Modifier.height(dimensions.padding.extraBig))

        FilenameInput(filename = state.filename) {
            onUiIntent(TrimmerUiIntent.FileSave.UpdateFilename(filename = it))
        }

        Spacer(Modifier.height(dimensions.padding.medium))

        FileSaveOptionsMenu(
            fileSaveOptions = fileSaveOptions,
            selectedSaveOptionIndex = state.selectedSaveOptionIndex,
            onItemClick = {
                onUiIntent(TrimmerUiIntent.FileSave.SelectSaveOption(saveOptionIndex = it))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensions.padding.medium),
        )

        Spacer(Modifier.height(dimensions.padding.extraBig))

        ConfirmButton(
            onUiIntent = onUiIntent,
            isSaveButtonClickable = state.isSaveButtonClickable,
            modifier = Modifier
                .padding(vertical = dimensions.padding.medium)
                .align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(dimensions.padding.medium))
    }
}
