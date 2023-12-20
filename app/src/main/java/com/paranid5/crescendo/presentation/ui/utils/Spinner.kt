package com.paranid5.crescendo.presentation.ui.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors

@Composable
fun Spinner(
    items: List<String>,
    selectedItemIndexes: List<Int>,
    onItemSelected: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
    dropdownModifier: Modifier = Modifier,
    previewItemIndex: Int = selectedItemIndexes.firstOrNull() ?: 0,
    selectedItemFactory: @Composable (Int, String, Modifier) -> Unit = { _, text, mod ->
        DefaultSelectedItem(text, mod)
    },
    previewItemFactory: @Composable (Int, String, Modifier) -> Unit = selectedItemFactory,
    dropdownItemFactory: @Composable (Int, String, Modifier) -> Unit = { _, text, mod ->
        DefaultItem(text, mod)
    },
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(modifier.clickable { isExpanded = true }) {
        previewItemFactory(
            previewItemIndex,
            items[previewItemIndex],
            modifier,
        )

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = dropdownModifier
        ) {
            items.forEachIndexed { index, element ->
                DropdownMenuItem(
                    text = {
                        when (index) {
                            in selectedItemIndexes -> selectedItemFactory(index, element, Modifier)
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
    val primaryColor = LocalAppColors.current.colorScheme.primary
    Text(text, modifier, primaryColor)
}

@Composable
private fun DefaultItem(text: String, modifier: Modifier = Modifier) = Text(text, modifier)