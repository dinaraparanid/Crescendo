package com.paranid5.mediastreamer.presentation.streaming

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.presentation.ui.theme.LocalAppColors
import org.koin.androidx.compose.get

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashPropertiesDialog(isDialogShownState: MutableState<Boolean>, modifier: Modifier = Modifier) {
    val filenameState = remember { mutableStateOf("") }
    val isButtonClickable by remember { derivedStateOf { filenameState.value.isNotEmpty() } }

    val fileSaveOptions = arrayOf(stringResource(R.string.audio), stringResource(R.string.video))
    val selectedSaveOptionIndexState = remember { mutableStateOf(0) }
    val isSaveAsVideo by remember { derivedStateOf { selectedSaveOptionIndexState.value == 1 } }

    if (isDialogShownState.value)
        AlertDialog(
            modifier = modifier,
            onDismissRequest = { isDialogShownState.value = false }
        ) {
            Column(Modifier.fillMaxSize()) {
                Title(Modifier.align(Alignment.CenterHorizontally))
                FilenameInput(filenameState = filenameState)
                SaveOptionsMenu(fileSaveOptions, selectedSaveOptionIndexState)
                ConfirmButton(isSaveAsVideo, isButtonClickable, filename = filenameState.value)
            }
        }
}

@Composable
private fun Title(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current.value

    Text(
        text = stringResource(R.string.cash_properties),
        modifier = modifier.padding(vertical = 15.dp),
        color = colors.primary,
        maxLines = 1,
        fontSize = 18.sp
    )
}

@Composable
private fun FilenameInput(filenameState: MutableState<String>, modifier: Modifier = Modifier) =
    Row(modifier.fillMaxWidth().padding(horizontal = 10.dp)) {
        Text("${stringResource(R.string.filename)}:")

        Spacer(Modifier.width(10.dp))

        TextField(
            value = filenameState.value,
            onValueChange = { filenameState.value = it },
            maxLines = 1,
            placeholder = { Text(stringResource(R.string.filename_placeholder)) }
        )
    }

@Composable
private fun SaveOptionsMenu(
    fileSaveOptions: Array<String>,
    selectedSaveOptionIndexState: MutableState<Int>,
    modifier: Modifier = Modifier
) {
    var isDropdownShown by remember { mutableStateOf(false) }

    Box(modifier.fillMaxWidth().padding(horizontal = 10.dp)) {
        Text(
            text = fileSaveOptions[selectedSaveOptionIndexState.value],
            modifier = Modifier.clickable { isDropdownShown = true }
        )

        DropdownMenu(expanded = isDropdownShown, onDismissRequest = { isDropdownShown = false }) {
            fileSaveOptions.forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = { selectedSaveOptionIndexState.value = index }
                )
            }
        }
    }
}

@Composable
private fun ConfirmButton(
    isSaveAsVideo: Boolean,
    isButtonClickable: Boolean,
    filename: String,
    modifier: Modifier = Modifier,
    streamingUIHandler: StreamingUIHandler = get()
) {
    Button(
        modifier = modifier,
        onClick = { streamingUIHandler.launchVideoCashService(filename, isSaveAsVideo) },
        enabled = isButtonClickable
    ) {
        Text(stringResource(R.string.start_cashing))
    }
}