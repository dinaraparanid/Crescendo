package com.paranid5.crescendo.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import kotlinx.collections.immutable.ImmutableList

@Composable
fun Spinner(
    items: ImmutableList<String>,
    selectedItemIndices: ImmutableList<Int>,
    onItemSelected: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
    dropdownModifier: Modifier = Modifier,
    previewItemIndex: Int = selectedItemIndices.firstOrNull() ?: 0,
    selectedItemFactory: @Composable (Int, String, Modifier) -> Unit = { _, text, mod ->
        DefaultSelectedItem(text, mod)
    },
    previewItemFactory: @Composable (Int, String, Modifier) -> Unit = { _, text, mod ->
        DefaultItem(text, mod.padding(vertical = dimensions.padding.small))
    },
    dropdownItemFactory: @Composable (Int, String, Modifier) -> Unit = { _, text, mod ->
        DefaultItem(text, mod)
    },
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(modifier.clickableWithRipple(bounded = true) { isExpanded = true }) {
        items.getOrNull(previewItemIndex)?.let {
            previewItemFactory(previewItemIndex, it, modifier)
        }

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = dropdownModifier.background(colors.background.highContrast)
        ) {
            items.forEachIndexed { index, element ->
                DropdownMenuItem(
                    text = {
                        when (index) {
                            in selectedItemIndices -> selectedItemFactory(index, element, Modifier)
                            else -> dropdownItemFactory(index, element, Modifier)
                        }
                    },
                    onClick = {
                        onItemSelected(index, element)
                        isExpanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun DefaultSelectedItem(text: String, modifier: Modifier = Modifier) {
    val updText by rememberUpdatedState(text)

    Text(
        text = updText,
        color = colors.selection.selected,
        style = typography.regular,
        modifier = modifier,
    )
}

@Composable
private fun DefaultItem(text: String, modifier: Modifier = Modifier) {
    val updText by rememberUpdatedState(text)

    Text(
        text = updText,
        modifier = modifier,
        color = colors.text.primary,
        style = typography.regular,
    )
}