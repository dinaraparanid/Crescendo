package com.paranid5.crescendo.presentation.main.trimmer.views.file_save_dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.domain.caching.CacheTrimRange

@Composable
fun FileSaveDialogContent(
    isDialogShownState: MutableState<Boolean>,
    filenameState: MutableState<String>,
    fileSaveOptions: Array<String>,
    selectedSaveOptionIndexState: MutableState<Int>,
    trackPath: String,
    trimRange: CacheTrimRange,
    isSaveButtonClickable: Boolean,
    modifier: Modifier = Modifier
) = Column(modifier) {
    Title(Modifier.align(Alignment.CenterHorizontally))
    Spacer(Modifier.height(10.dp))

    FilenameInput(filenameState = filenameState)
    Spacer(Modifier.height(10.dp))

    FileSaveOptionsMenu(
        fileSaveOptions = fileSaveOptions,
        selectedSaveOptionIndexState = selectedSaveOptionIndexState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    )

    Spacer(Modifier.height(10.dp))

    ConfirmButton(
        isDialogShownState = isDialogShownState,
        trimRange = trimRange,
        isClickable = isSaveButtonClickable,
        trackPath = trackPath,
        modifier = Modifier
            .padding(vertical = 10.dp)
            .align(Alignment.CenterHorizontally)
    )
}