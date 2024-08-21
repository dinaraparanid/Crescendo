package com.paranid5.crescendo.cache.presentation.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography

@Composable
internal fun CacheDialogLabel(modifier: Modifier = Modifier) =
    Text(
        text = stringResource(R.string.cache_dialog_title),
        modifier = modifier,
        color = colors.text.primary,
        style = typography.h.h3,
        fontWeight = FontWeight.Bold,
    )
