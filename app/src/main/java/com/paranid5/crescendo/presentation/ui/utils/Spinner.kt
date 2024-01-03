package com.paranid5.crescendo.presentation.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
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
    previewItemFactory: @Composable (Int, String, Modifier) -> Unit = selectedItemFactory,
    dropdownItemFactory: @Composable (Int, String, Modifier) -> Unit = { _, text, mod ->
        DefaultItem(text, mod)
    },
) {
    val colors = LocalAppColors.current
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
            modifier = dropdownModifier.background(colors.background)
        ) {
            items.forEachIndexed { index, element ->
                DropdownMenuItem(
                    text = {
                        when (index) {
                            in selectedItemIndices ->
                                selectedItemFactory(index, element, Modifier)

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
    val colors = LocalAppColors.current
    val updText by rememberUpdatedState(text)

    Text(
        text = updText,
        modifier = modifier,
        color = colors.primary,
        fontSize = 14.sp
    )
}

@Composable
private fun DefaultItem(text: String, modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current
    val updText by rememberUpdatedState(text)

    Text(
        text = updText,
        modifier = modifier,
        color = colors.fontColor,
        fontSize = 14.sp
    )
}