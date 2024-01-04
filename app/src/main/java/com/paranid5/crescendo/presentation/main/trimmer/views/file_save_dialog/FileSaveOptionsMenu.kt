package com.paranid5.crescendo.presentation.main.trimmer.views.file_save_dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors

@Composable
fun FileSaveOptionsMenu(
    fileSaveOptions: Array<String>,
    selectedSaveOptionIndexState: MutableState<Int>,
    modifier: Modifier = Modifier
) {
    val isDropdownShownState = remember { mutableStateOf(false) }
    val selectedSaveOptionIndex by selectedSaveOptionIndexState

    Box(modifier) {
        OptionLabel(
            fileSaveOptions = fileSaveOptions,
            selectedSaveOptionIndex = selectedSaveOptionIndex,
            isDropdownShownState = isDropdownShownState,
            modifier = Modifier.align(Alignment.Center)
        )

        FileOptionsMenu(
            fileSaveOptions = fileSaveOptions,
            selectedSaveOptionIndexState = selectedSaveOptionIndexState,
            isDropdownShownState = isDropdownShownState
        )
    }
}

@Composable
private fun OptionLabel(
    fileSaveOptions: Array<String>,
    selectedSaveOptionIndex: Int,
    isDropdownShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    var isDropdownShown by isDropdownShownState

    val label by remember(fileSaveOptions, selectedSaveOptionIndex) {
        derivedStateOf { fileSaveOptions[selectedSaveOptionIndex] }
    }

    Text(
        text = label,
        color = colors.primary,
        modifier = modifier.clickable { isDropdownShown = true },
    )
}

@Composable
private fun FileOptionsMenu(
    fileSaveOptions: Array<String>,
    selectedSaveOptionIndexState: MutableState<Int>,
    isDropdownShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    var isDropdownShown by isDropdownShownState

    DropdownMenu(
        modifier = modifier.background(colors.background),
        expanded = isDropdownShown,
        onDismissRequest = { isDropdownShown = false }
    ) {
        fileSaveOptions.forEachIndexed { index, option ->
            FileOption(
                option = option,
                index = index,
                selectedSaveOptionIndexState = selectedSaveOptionIndexState
            )
        }
    }
}

@Composable
private fun FileOption(
    option: String,
    index: Int,
    selectedSaveOptionIndexState: MutableState<Int>,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    var selectedSaveOptionIndex by selectedSaveOptionIndexState

    DropdownMenuItem(
        modifier = modifier,
        text = { Text(text = option, color = colors.fontColor) },
        onClick = { selectedSaveOptionIndex = index },
    )
}