package com.paranid5.crescendo.trimmer.presentation.ui.file_save_dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.ui.foundation.AppOutlinedTextField

@Composable
internal fun FilenameInput(
    filename: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
) = AppOutlinedTextField(
    value = filename,
    onValueChange = onValueChange,
    label = { FilenameLabel() },
    modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = dimensions.padding.medium),
)

@Composable
private fun FilenameLabel(modifier: Modifier = Modifier) =
    Text(
        text = stringResource(R.string.filename),
        style = typography.caption,
        modifier = modifier,
    )