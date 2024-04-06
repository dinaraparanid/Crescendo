package com.paranid5.crescendo.tracks.presentation.views.bar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors

@Composable
internal fun OrderItem(item: String, modifier: Modifier = Modifier) =
    Row(modifier) {
        OrderItemCheckbox()
        Spacer(Modifier.width(10.dp))
        OrderItemLabel(item)
    }

@Composable
private fun OrderItemCheckbox(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    Checkbox(
        checked = false,
        onCheckedChange = null,
        modifier = modifier,
        colors = CheckboxDefaults.colors(uncheckedColor = colors.fontColor),
    )
}

@Composable
private fun OrderItemLabel(item: String, modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    Text(
        text = item,
        color = colors.fontColor,
        fontSize = 14.sp,
        modifier = modifier
    )
}