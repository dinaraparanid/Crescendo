package com.paranid5.crescendo.trimmer.presentation.views.file_save_dialog

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography

@Composable
internal fun Title(modifier: Modifier = Modifier) =
    Text(
        text = stringResource(R.string.save_as),
        modifier = modifier.padding(vertical = dimensions.padding.extraMedium),
        color = colors.primary,
        style = typography.h.h2,
        maxLines = 1,
        fontWeight = FontWeight.Bold,
    )
