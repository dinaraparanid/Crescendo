package com.paranid5.crescendo.tracks.presentation.ui.bar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.ui.foundation.AppText

@Composable
internal fun OrderItem(item: String, modifier: Modifier = Modifier) =
    Row(modifier) {
        OrderItemCheckbox()
        Spacer(Modifier.width(dimensions.padding.medium))
        OrderItemLabel(item)
    }

@Composable
private fun OrderItemCheckbox(modifier: Modifier = Modifier) =
    Checkbox(
        checked = false,
        onCheckedChange = null,
        modifier = modifier,
        colors = CheckboxDefaults.colors(uncheckedColor = colors.text.primary),
    )

@Composable
private fun OrderItemLabel(item: String, modifier: Modifier = Modifier) =
    AppText(
        text = item,
        style = typography.regular.copy(color = colors.text.primary),
        modifier = modifier,
    )