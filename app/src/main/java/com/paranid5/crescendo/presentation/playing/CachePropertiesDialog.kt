package com.paranid5.crescendo.presentation.playing

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.R
import com.paranid5.crescendo.domain.StorageHandler
import com.paranid5.crescendo.domain.services.video_cache_service.CacheTrimRange
import com.paranid5.crescendo.domain.services.video_cache_service.Formats
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CachePropertiesDialog(
    isDialogShownState: MutableState<Boolean>,
    playingPresenter: PlayingPresenter,
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = koinInject()
) {
    val colors = LocalAppColors.current.value
    val currentMetadata by storageHandler.currentMetadataState.collectAsState()

    val lengthInSecs by remember {
        derivedStateOf {
            currentMetadata?.lenInMillis?.let { it / 1000 } ?: 0
        }
    }

    val trimOffsetSecsState = remember { mutableLongStateOf(0) }
    val endPointSecsState = remember { mutableLongStateOf(lengthInSecs) }

    val trimOffsetSecs by trimOffsetSecsState
    val endPointSecs by endPointSecsState

    val trimRange by remember {
        derivedStateOf { CacheTrimRange(trimOffsetSecs, endPointSecs) }
    }

    val filenameState = remember { mutableStateOf("") }
    val isButtonClickable by remember { derivedStateOf { filenameState.value.isNotEmpty() } }

    val fileSaveOptions = arrayOf(
        stringResource(R.string.mp3),
        stringResource(R.string.wav),
        stringResource(R.string.aac),
        stringResource(R.string.mp4)
    )

    val selectedSaveOptionIndexState = remember { mutableIntStateOf(0) }

    val format by remember {
        derivedStateOf { Formats.entries[selectedSaveOptionIndexState.intValue] }
    }

    if (isDialogShownState.value)
        AlertDialog(onDismissRequest = { isDialogShownState.value = false }) {
            Card(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.background)
            ) {
                Column(Modifier.fillMaxWidth()) {
                    Title(Modifier.align(Alignment.CenterHorizontally))
                    Spacer(Modifier.height(10.dp))

                    FilenameInput(filenameState = filenameState)
                    Spacer(Modifier.height(10.dp))

                    SaveOptionsMenu(fileSaveOptions, selectedSaveOptionIndexState)
                    Spacer(Modifier.height(10.dp))

                    ConfirmButton(
                        isDialogShownState = isDialogShownState,
                        format = format,
                        trimRange = trimRange,
                        isButtonClickable = isButtonClickable,
                        filename = filenameState.value,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
}

@Composable
private fun Title(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current.value

    Text(
        text = stringResource(R.string.cache_properties),
        modifier = modifier.padding(vertical = 15.dp),
        color = colors.primary,
        maxLines = 1,
        fontSize = 18.sp
    )
}

@Composable
private fun FilenameInput(filenameState: MutableState<String>, modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current.value

    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        Text(
            text = "${stringResource(R.string.filename)}:",
            modifier = Modifier.align(Alignment.CenterVertically),
            color = colors.inverseSurface
        )

        Spacer(Modifier.width(10.dp))

        TextField(
            value = filenameState.value,
            onValueChange = { filenameState.value = it },
            maxLines = 1,
            placeholder = { Text(stringResource(R.string.filename_placeholder)) },
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }
}

@Composable
private fun SaveOptionsMenu(
    fileSaveOptions: Array<String>,
    selectedSaveOptionIndexState: MutableState<Int>,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current.value
    var isDropdownShown by remember { mutableStateOf(false) }

    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        Text(
            text = "${stringResource(R.string.save_as)}:",
            modifier = Modifier.align(Alignment.CenterVertically),
            color = colors.inverseSurface
        )

        Spacer(Modifier.width(10.dp))

        Box(Modifier.fillMaxWidth()) {
            Text(
                text = fileSaveOptions[selectedSaveOptionIndexState.value],
                color = colors.primary,
                modifier = Modifier
                    .align(Alignment.Center)
                    .clickable { isDropdownShown = true },
            )

            DropdownMenu(
                expanded = isDropdownShown,
                onDismissRequest = { isDropdownShown = false }
            ) {
                fileSaveOptions.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = { selectedSaveOptionIndexState.value = index }
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfirmButton(
    isDialogShownState: MutableState<Boolean>,
    format: Formats,
    trimRange: CacheTrimRange,
    isButtonClickable: Boolean,
    filename: String,
    modifier: Modifier = Modifier,
    playingUIHandler: PlayingUIHandler = koinInject()
) {
    val colors = LocalAppColors.current.value

    Button(
        modifier = modifier.padding(vertical = 10.dp),
        onClick = {
            playingUIHandler.launchVideoCashService(filename, format, trimRange)
            isDialogShownState.value = false
        },
        enabled = isButtonClickable,
        colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
    ) {
        Text(stringResource(R.string.start_caching))
    }
}