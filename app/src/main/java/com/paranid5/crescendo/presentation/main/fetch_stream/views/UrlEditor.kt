package com.paranid5.crescendo.presentation.main.fetch_stream.views

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.presentation.main.fetch_stream.FetchStreamViewModel
import com.paranid5.crescendo.presentation.main.fetch_stream.properties.compose.collectCurrentTextAsState
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors
import com.paranid5.crescendo.presentation.ui.utils.DefaultOutlinedTextField

@Composable
fun UrlEditor(
    modifier: Modifier = Modifier,
    viewModel: FetchStreamViewModel = koinActivityViewModel(),
) {
    val currentText by viewModel.collectCurrentTextAsState()

    DefaultOutlinedTextField(
        value = currentText,
        modifier = modifier,
        label = { UrlEditorLabel() },
        onValueChange = { query -> viewModel.setCurrentText(query) }
    )
}

@Composable
private fun UrlEditorLabel(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    Text(
        text = stringResource(R.string.enter_stream_url),
        color = colors.primary,
        fontSize = 12.sp,
        modifier = modifier
    )
}