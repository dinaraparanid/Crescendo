package com.paranid5.crescendo.trimmer.presentation.ui.file_save_dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.ui.foundation.AppText
import com.paranid5.crescendo.ui.utils.clickableWithRipple
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun FileSaveOptionsMenu(
    fileSaveOptions: ImmutableList<String>,
    selectedSaveOptionIndex: Int,
    modifier: Modifier = Modifier,
    onItemClick: (index: Int) -> Unit,
) {
    val isDropdownShownState = remember { mutableStateOf(false) }

    Box(modifier) {
        OptionLabel(
            fileSaveOptions = fileSaveOptions,
            selectedSaveOptionIndex = selectedSaveOptionIndex,
            isDropdownShownState = isDropdownShownState,
            modifier = Modifier.align(Alignment.Center),
        )

        FileOptionsMenu(
            fileSaveOptions = fileSaveOptions,
            isDropdownShownState = isDropdownShownState,
            onItemClick = onItemClick,
        )
    }
}

@Composable
private fun OptionLabel(
    fileSaveOptions: ImmutableList<String>,
    selectedSaveOptionIndex: Int,
    isDropdownShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
) {
    var isDropdownShown by isDropdownShownState

    val label by remember(fileSaveOptions, selectedSaveOptionIndex) {
        derivedStateOf { fileSaveOptions[selectedSaveOptionIndex] }
    }

    AppText(
        text = label,
        modifier = modifier.clickableWithRipple { isDropdownShown = true },
        style = typography.regular.copy(
            color = colors.text.primary,
        ),
    )
}

@Composable
private fun FileOptionsMenu(
    fileSaveOptions: ImmutableList<String>,
    isDropdownShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    onItemClick: (index: Int) -> Unit,
) {
    var isDropdownShown by isDropdownShownState

    DropdownMenu(
        modifier = modifier.background(colors.background.primary),
        expanded = isDropdownShown,
        onDismissRequest = { isDropdownShown = false },
    ) {
        fileSaveOptions.forEachIndexed { index, option ->
            FileOption(
                option = option,
                index = index,
                onItemClick = onItemClick,
            )
        }
    }
}

@Composable
private fun FileOption(
    option: String,
    index: Int,
    modifier: Modifier = Modifier,
    onItemClick: (index: Int) -> Unit,
) = DropdownMenuItem(
    modifier = modifier,
    text = {
        AppText(
            text = option,
            style = typography.regular.copy(
                color = colors.text.primary,
            ),
        )
    },
    onClick = { onItemClick(index) },
)
