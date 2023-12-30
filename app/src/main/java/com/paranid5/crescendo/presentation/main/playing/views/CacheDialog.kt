package com.paranid5.crescendo.presentation.main.playing.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.R
import com.paranid5.crescendo.domain.caching.Formats
import com.paranid5.crescendo.domain.trimming.TrimRange
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.presentation.main.playing.PlayingViewModel
import com.paranid5.crescendo.presentation.main.playing.properties.compose.collectCurrentMetadataAsState
import com.paranid5.crescendo.presentation.main.playing.views.cache.CacheDialogContent

@Composable
fun CacheDialog(
    isDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
) {
    val durationMillis by rememberDurationMillis()
    val trimOffsetMillisState = remember { mutableLongStateOf(0) }
    val totalDurationMillisState = remember(durationMillis) { mutableLongStateOf(durationMillis) }

    val trimOffsetMillis by trimOffsetMillisState
    val totalDurationMillis by totalDurationMillisState
    val trimRange by rememberTrimRange(trimOffsetMillis, totalDurationMillis)

    val filenameState = remember { mutableStateOf("") }
    val filename by filenameState

    val isButtonClickable by rememberIsCacheButtonClickable(filename)

    val fileSaveOptions = arrayOf(
        stringResource(R.string.mp3),
        stringResource(R.string.wav),
        stringResource(R.string.aac),
        stringResource(R.string.mp4)
    )

    val selectedSaveOptionIndexState = remember { mutableIntStateOf(0) }
    val selectedSaveOptionIndex by selectedSaveOptionIndexState
    val format by rememberCacheFormat(selectedSaveOptionIndex)

    val isDialogShown by isDialogShownState

    if (isDialogShown)
        CacheDialogContent(
            fileSaveOptions = fileSaveOptions,
            format = format,
            trimRange = trimRange,
            isButtonClickable = isButtonClickable,
            isDialogShownState = isDialogShownState,
            filenameState = filenameState,
            selectedSaveOptionIndexState = selectedSaveOptionIndexState,
            modifier = modifier
        )
}

@Composable
private fun rememberDurationMillis(
    viewModel: PlayingViewModel = koinActivityViewModel(),
): State<Long> {
    val currentMetadata by viewModel.collectCurrentMetadataAsState()

    return remember(currentMetadata) {
        derivedStateOf { currentMetadata?.durationMillis ?: 0 }
    }
}

@Composable
private fun rememberTrimRange(trimOffsetMillis: Long, totalDurationMillis: Long) =
    remember(trimOffsetMillis, totalDurationMillis) {
        derivedStateOf {
            TrimRange(
                startPointMillis = trimOffsetMillis,
                totalDurationMillis = totalDurationMillis
            )
        }
    }

@Composable
private fun rememberIsCacheButtonClickable(filename: String) = remember(filename) {
    derivedStateOf { filename.isNotBlank() }
}

@Composable
private fun rememberCacheFormat(selectedSaveOptionIndex: Int) =
    remember(selectedSaveOptionIndex) {
        derivedStateOf { Formats.entries[selectedSaveOptionIndex] }
    }