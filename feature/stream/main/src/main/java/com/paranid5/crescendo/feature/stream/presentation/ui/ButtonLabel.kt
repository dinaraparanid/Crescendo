package com.paranid5.crescendo.feature.stream.presentation.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography

@Composable
internal fun ButtonLabel(text: String, modifier: Modifier = Modifier) =
    Text(
        text = text,
        color = colors.text.primary,
        style = typography.body,
        fontWeight = FontWeight.Bold,
        modifier = modifier,
    )