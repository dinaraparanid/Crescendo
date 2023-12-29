package com.paranid5.crescendo.presentation.main.playing.views.cache

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors

@Composable
fun SaveOptionsMenu(
    fileSaveOptions: Array<String>,
    selectedSaveOptionIndexState: MutableState<Int>,
    modifier: Modifier = Modifier
) {
    val selectedSaveOptionIndex by selectedSaveOptionIndexState
    val isDropdownShownState = remember { mutableStateOf(false) }

    Box(modifier) {
        OptionLabel(
            fileSaveOptions = fileSaveOptions,
            isDropdownShownState = isDropdownShownState,
            selectedSaveOptionIndex = selectedSaveOptionIndex,
            modifier = Modifier.align(Alignment.Center)
        )

        OptionsMenu(
            fileSaveOptions = fileSaveOptions,
            isDropdownShownState = isDropdownShownState,
            selectedSaveOptionIndexState = selectedSaveOptionIndexState
        )
    }
}

@Composable
private fun OptionLabel(
    fileSaveOptions: Array<String>,
    isDropdownShownState: MutableState<Boolean>,
    selectedSaveOptionIndex: Int,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    var isDropdownShown by isDropdownShownState

    Text(
        text = fileSaveOptions[selectedSaveOptionIndex],
        color = colors.primary,
        modifier = modifier.clickable { isDropdownShown = true },
    )
}

@Composable
private fun OptionsMenu(
    fileSaveOptions: Array<String>,
    isDropdownShownState: MutableState<Boolean>,
    selectedSaveOptionIndexState: MutableState<Int>,
    modifier: Modifier = Modifier
) {
    var isDropdownShown by isDropdownShownState

    DropdownMenu(
        modifier = modifier,
        expanded = isDropdownShown,
        onDismissRequest = { isDropdownShown = false }
    ) {
        fileSaveOptions.forEachIndexed { index, item ->
            OptionMenuItem(index, item, selectedSaveOptionIndexState)
        }
    }
}

@Composable
private fun OptionMenuItem(
    index: Int,
    item: String,
    selectedSaveOptionIndexState: MutableState<Int>,
    modifier: Modifier = Modifier
) {
    var selectedSaveOptionIndex by selectedSaveOptionIndexState

    DropdownMenuItem(
        modifier = modifier,
        text = { OptionMenuItemLabel(item) },
        onClick = { selectedSaveOptionIndex = index },
    )
}

@Composable
private fun OptionMenuItemLabel(
    item: String,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current

    Text(
        text = item,
        color = colors.fontColor,
        fontSize = 14.sp,
        modifier = modifier
    )
}