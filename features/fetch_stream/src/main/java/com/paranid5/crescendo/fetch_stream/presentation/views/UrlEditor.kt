package com.paranid5.crescendo.fetch_stream.presentation.views

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.fetch_stream.presentation.FetchStreamViewModel
import com.paranid5.crescendo.fetch_stream.presentation.properties.compose.collectCurrentTextAsState
import com.paranid5.crescendo.ui.foundation.AppOutlinedTextField
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun UrlEditor(
    modifier: Modifier = Modifier,
    viewModel: FetchStreamViewModel = koinViewModel(),
) {
    val currentText by viewModel.collectCurrentTextAsState()

    AppOutlinedTextField(
        value = currentText,
        modifier = modifier,
        label = { UrlEditorLabel() },
        onValueChange = { viewModel.setCurrentText(currentText = it) },
    )
}

@Composable
private fun UrlEditorLabel(modifier: Modifier = Modifier) =
    Text(
        text = stringResource(R.string.enter_stream_url),
        color = colors.primary,
        style = typography.caption,
        modifier = modifier,
    )