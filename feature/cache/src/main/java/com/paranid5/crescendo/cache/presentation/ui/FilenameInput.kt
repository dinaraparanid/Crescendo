package com.paranid5.crescendo.cache.presentation.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.cache.view_model.CacheState
import com.paranid5.crescendo.cache.view_model.CacheUiIntent
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.ui.foundation.AppOutlinedTextField

@Composable
internal fun FilenameInput(
    state: CacheState,
    onUiIntent: (CacheUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = AppOutlinedTextField(
    value = state.filename,
    onValueChange = { onUiIntent(CacheUiIntent.UpdateFilename(filename = it)) },
    label = { FilenameInputLabel() },
    modifier = modifier,
)

@Composable
private fun FilenameInputLabel(modifier: Modifier = Modifier) =
    Text(
        text = stringResource(R.string.cache_dialog_filename),
        color = colors.text.primary,
        style = typography.caption,
        modifier = modifier,
    )
