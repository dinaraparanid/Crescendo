package com.paranid5.crescendo.presentation.main.playing.views.cache

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.presentation.main.playing.PlayingViewModel
import com.paranid5.crescendo.presentation.main.playing.properties.compose.collectSelectedSaveOptionIndexAsState
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import kotlinx.collections.immutable.persistentListOf

@Composable
fun SaveOptionsMenu(modifier: Modifier = Modifier) {
    val isDropdownShownState = remember { mutableStateOf(false) }

    Box(modifier) {
        OptionLabel(
            isDropdownShownState = isDropdownShownState,
            modifier = Modifier.align(Alignment.Center)
        )

        OptionsMenu(isDropdownShownState = isDropdownShownState)
    }
}

@Composable
private fun OptionLabel(
    isDropdownShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    viewModel: PlayingViewModel = koinActivityViewModel()
) {
    val colors = LocalAppColors.current
    val selectedSaveOptionIndex by viewModel.collectSelectedSaveOptionIndexAsState()
    var isDropdownShown by isDropdownShownState

    Text(
        text = fileSaveOptions[selectedSaveOptionIndex],
        color = colors.primary,
        modifier = modifier.clickable { isDropdownShown = true },
    )
}

@Composable
private fun OptionsMenu(
    isDropdownShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    var isDropdownShown by isDropdownShownState

    DropdownMenu(
        modifier = modifier.background(color = colors.background),
        expanded = isDropdownShown,
        onDismissRequest = { isDropdownShown = false }
    ) {
        fileSaveOptions.forEachIndexed { index, item ->
            OptionMenuItem(index, item)
        }
    }
}

@Composable
private fun OptionMenuItem(
    index: Int,
    item: String,
    modifier: Modifier = Modifier,
    viewModel: PlayingViewModel = koinActivityViewModel()
) {
    val colors = LocalAppColors.current

    DropdownMenuItem(
        modifier = modifier,
        colors = MenuDefaults.itemColors(textColor = colors.fontColor),
        text = { OptionMenuItemLabel(item) },
        onClick = { viewModel.setSelectedSaveOptionIndex(index) },
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

private inline val fileSaveOptions
    @Composable
    get() = persistentListOf(
        stringResource(R.string.mp3),
        stringResource(R.string.wav),
        stringResource(R.string.aac),
        stringResource(R.string.mp4)
    )