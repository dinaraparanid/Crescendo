package com.paranid5.crescendo.cache.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.cache.view_model.CacheState
import com.paranid5.crescendo.cache.view_model.CacheUiIntent
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.ui.utils.clickableWithRipple
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun SaveOptionsMenu(
    state: CacheState,
    onUiIntent: (CacheUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isDropdownMenuVisible by remember { mutableStateOf(false) }

    Box(modifier) {
        OptionLabel(
            selectedSaveOptionIndex = state.selectedSaveOptionIndex,
            modifier = Modifier.align(Alignment.Center),
            onClick = { isDropdownMenuVisible = true },
        )

        OptionsMenu(
            isVisible = isDropdownMenuVisible,
            onDismiss = { isDropdownMenuVisible = false },
        ) {
            onUiIntent(CacheUiIntent.UpdateSelectedSaveOptionIndex(selectedSaveOptionIndex = it))
        }
    }
}

@Composable
private fun OptionLabel(
    selectedSaveOptionIndex: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) = Text(
    text = fileSaveOptions[selectedSaveOptionIndex],
    color = colors.text.primary,
    style = typography.regular,
    modifier = modifier.clickableWithRipple(onClick = onClick),
)

@Composable
private fun OptionsMenu(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onItemClick: (index: Int) -> Unit,
) = DropdownMenu(
    modifier = modifier.background(color = colors.background.primary),
    expanded = isVisible,
    onDismissRequest = onDismiss,
) {
    fileSaveOptions.forEachIndexed { index, item ->
        OptionMenuItem(index = index, item = item, onItemClick = onItemClick)
    }
}

@Composable
private fun OptionMenuItem(
    index: Int,
    item: String,
    modifier: Modifier = Modifier,
    onItemClick: (index: Int) -> Unit,
) = DropdownMenuItem(
    modifier = modifier,
    colors = MenuDefaults.itemColors(textColor = colors.text.primary),
    text = { OptionMenuItemLabel(item = item) },
    onClick = { onItemClick(index) },
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
