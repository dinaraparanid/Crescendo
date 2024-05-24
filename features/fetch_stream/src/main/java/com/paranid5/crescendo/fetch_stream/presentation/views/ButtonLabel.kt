package com.paranid5.crescendo.fetch_stream.presentation.views

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors

@Composable
internal fun ButtonLabel(text: String, modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    Text(
        text = text,
        color = colors.fontColor,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}