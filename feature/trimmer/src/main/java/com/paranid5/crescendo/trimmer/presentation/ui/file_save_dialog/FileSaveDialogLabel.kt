package com.paranid5.crescendo.trimmer.presentation.ui.file_save_dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.ui.foundation.AppText

@Composable
internal fun FileSaveDialogLabel(modifier: Modifier = Modifier) =
    AppText(
        text = stringResource(R.string.save_as),
        modifier = modifier,
        style = typography.h.h4.copy(
            color = colors.text.primary,
            fontWeight = FontWeight.W700,
        ),
    )
