package com.paranid5.mediastreamer.presentation.ui.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.paranid5.mediastreamer.presentation.ui.theme.LocalAppColors

@Composable
fun Spinner(
    items: List<String>,
    selectedItemInd: Int,
    onItemSelected: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
    dropDownModifier: Modifier = Modifier,
    selectedItemFactory: @Composable (Int, String, Modifier) -> Unit = { _, text, mod ->
        DefaultSelectedItem(text, mod)
    },
    dropdownItemFactory: @Composable (Int, String, Modifier) -> Unit = { _, text, mod ->
        DefaultItem(text, mod)
    },
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(modifier.wrapContentSize(Alignment.TopStart)) {
        selectedItemFactory(
            selectedItemInd,
            items[selectedItemInd],
            Modifier.clickable { isExpanded = true },
        )

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = dropDownModifier
        ) {
            items.forEachIndexed { index, element ->
                DropdownMenuItem(
                    text = { dropdownItemFactory(index, element, Modifier) },
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
    val primaryColor = LocalAppColors.current.value.primary
    Text(text, modifier, primaryColor)
}

@Composable
private fun DefaultItem(text: String, modifier: Modifier = Modifier) =
    Text(text, modifier)