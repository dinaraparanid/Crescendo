package com.paranid5.crescendo.tracks.presentation.views.bar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors

@Composable
internal fun SelectedOrderItem(item: String, modifier: Modifier = Modifier) =
    Row(modifier) {
        SelectedOrderItemCheckbox(Modifier.align(Alignment.CenterVertically))

        Spacer(Modifier.width(10.dp))

        SelectedOrderItemLabel(
            item = item,
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }

@Composable
private fun SelectedOrderItemCheckbox(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    Checkbox(
        checked = true,
        onCheckedChange = null,
        modifier = modifier,
        colors = CheckboxDefaults.colors(
            checkedColor = colors.primary,
            checkmarkColor = colors.background,
        ),
    )
}

@Composable
private fun SelectedOrderItemLabel(item: String, modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    Text(
        text = item,
        color = colors.primary,
        fontSize = 14.sp,
        modifier = modifier,
    )
}