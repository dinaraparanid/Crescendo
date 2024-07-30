package com.paranid5.crescendo.cache.presentation.view

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
import com.paranid5.crescendo.cache.presentation.CacheViewModel
import com.paranid5.crescendo.cache.presentation.properties.collectSelectedSaveOptionIndexAsState
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import kotlinx.collections.immutable.persistentListOf
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun SaveOptionsMenu(modifier: Modifier = Modifier) {
    val isDropdownShownState = remember { mutableStateOf(false) }

    Box(modifier) {
        OptionLabel(
            isDropdownShownState = isDropdownShownState,
            modifier = Modifier.align(Alignment.Center),
        )

        OptionsMenu(isDropdownShownState = isDropdownShownState)
    }
}

@Composable
private fun OptionLabel(
    isDropdownShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    viewModel: CacheViewModel = koinViewModel()
) {
    val selectedSaveOptionIndex by viewModel.collectSelectedSaveOptionIndexAsState()
    var isDropdownShown by isDropdownShownState

    Text(
        text = fileSaveOptions[selectedSaveOptionIndex],
        color = colors.primary,
        style = typography.regular,
        modifier = modifier.clickable { isDropdownShown = true },
    )
}

@Composable
private fun OptionsMenu(
    isDropdownShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier
) {
    var isDropdownShown by isDropdownShownState

    DropdownMenu(
        modifier = modifier.background(color = colors.background.primary),
        expanded = isDropdownShown,
        onDismissRequest = { isDropdownShown = false },
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
    viewModel: CacheViewModel = koinViewModel()
) = DropdownMenuItem(
    modifier = modifier,
    colors = MenuDefaults.itemColors(textColor = colors.text.primary),
    text = { OptionMenuItemLabel(item) },
    onClick = { viewModel.setSelectedSaveOptionIndex(index) },
)

@Composable
private fun OptionMenuItemLabel(
    item: String,
    modifier: Modifier = Modifier,
) = Text(
    text = item,
    color = colors.text.primary,
    style = typography.regular,
    modifier = modifier,
)

private inline val fileSaveOptions
    @Composable
    get() = persistentListOf(
        stringResource(R.string.mp3),
        stringResource(R.string.wav),
        stringResource(R.string.aac),
        stringResource(R.string.mp4),
    )