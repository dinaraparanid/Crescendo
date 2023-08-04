package com.paranid5.crescendo.presentation.playing

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.R
import com.paranid5.crescendo.domain.services.video_cash_service.Formats
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashPropertiesDialog(
    isDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current.value
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
        derivedStateOf { Formats.values()[selectedSaveOptionIndexState.value] }
    }

    if (isDialogShownState.value)
        AlertDialog(
            onDismissRequest = { isDialogShownState.value = false },
            modifier = modifier
                .background(colors.background)
                .wrapContentSize()
                .border(
                    width = 30.dp,
                    color = Color.Transparent,
                    shape = RoundedCornerShape(30.dp)
                ),
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
                    isButtonClickable = isButtonClickable,
                    filename = filenameState.value,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
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
        Text(
            text = "${stringResource(R.string.filename)}:",
            modifier = Modifier.align(Alignment.CenterVertically)
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

@Composable
private fun SaveOptionsMenu(
    fileSaveOptions: Array<String>,
    selectedSaveOptionIndexState: MutableState<Int>,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current.value
    var isDropdownShown by remember { mutableStateOf(false) }

    Row(modifier.fillMaxWidth().padding(horizontal = 10.dp)) {
        Text(
            text = "${stringResource(R.string.save_as)}:",
            modifier = Modifier.align(Alignment.CenterVertically)
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
    isButtonClickable: Boolean,
    filename: String,
    modifier: Modifier = Modifier,
    playingUIHandler: PlayingUIHandler = koinInject()
) {
    val colors = LocalAppColors.current.value

    Button(
        modifier = modifier.padding(vertical = 10.dp),
        onClick = {
            playingUIHandler.launchVideoCashService(filename, format)
            isDialogShownState.value = false
        },
        enabled = isButtonClickable,
        colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
    ) {
        Text(stringResource(R.string.start_cashing))
    }
}