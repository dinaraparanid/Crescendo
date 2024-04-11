package com.paranid5.crescendo.trimmer.presentation.views.file_save_dialog

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
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.common.caching.Formats
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.core.common.trimming.FadeDurations
import com.paranid5.crescendo.core.common.trimming.PitchAndSpeed
import com.paranid5.crescendo.core.common.trimming.TrimRange

@Composable
internal fun FileSaveDialogContent(
    fileSaveOptions: Array<String>,
    track: Track,
    audioFormat: Formats,
    pitchAndSpeed: PitchAndSpeed,
    trimRange: TrimRange,
    fadeDurations: FadeDurations,
    isSaveButtonClickable: Boolean,
    isDialogShownState: MutableState<Boolean>,
    filenameState: MutableState<String>,
    selectedSaveOptionIndexState: MutableState<Int>,
    modifier: Modifier = Modifier
) {
    val outputFilename by filenameState

    Column(modifier) {
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
            isSaveButtonClickable = isSaveButtonClickable,
            track = track,
            outputFilename = outputFilename,
            audioFormat = audioFormat,
            trimRange = trimRange,
            fadeDurations = fadeDurations,
            pitchAndSpeed = pitchAndSpeed,
            isDialogShownState = isDialogShownState,
            modifier = Modifier
                .padding(vertical = 10.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}