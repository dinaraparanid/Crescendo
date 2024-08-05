package com.paranid5.crescendo.tracks.presentation.ui.bar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography

@Composable
internal fun SelectedOrderItem(item: String, modifier: Modifier = Modifier) =
    Row(modifier) {
        SelectedOrderItemCheckbox(Modifier.align(Alignment.CenterVertically))

        Spacer(Modifier.width(dimensions.padding.medium))

        SelectedOrderItemLabel(
            item = item,
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }

@Composable
private fun SelectedOrderItemCheckbox(modifier: Modifier = Modifier) =
    Checkbox(
        checked = true,
        onCheckedChange = null,
        modifier = modifier,
        colors = CheckboxDefaults.colors(
            checkedColor = colors.primary,
            checkmarkColor = colors.background.primary,
        ),
    )

@Composable
private fun SelectedOrderItemLabel(item: String, modifier: Modifier = Modifier) =
    Text(
        text = item,
        color = colors.selection.selected,
        style = typography.regular,
        modifier = modifier,
    )