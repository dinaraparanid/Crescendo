package com.paranid5.crescendo.cache.presentation.view

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.cache.presentation.CacheViewModel
import com.paranid5.crescendo.cache.presentation.properties.collectFilenameAsState
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors
import com.paranid5.crescendo.ui.utils.AppOutlinedTextField
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun FilenameInput(
    modifier: Modifier = Modifier,
    viewModel: CacheViewModel = koinViewModel()
) {
    val filename by viewModel.collectFilenameAsState()

    AppOutlinedTextField(
        value = filename,
        onValueChange = viewModel::setFilename,
        label = { FilenameInputLabel() },
        modifier = modifier
    )
}

@Composable
private fun FilenameInputLabel(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    Text(
        text = stringResource(R.string.filename),
        color = colors.primary,
        fontSize = 12.sp,
        modifier = modifier
    )
}