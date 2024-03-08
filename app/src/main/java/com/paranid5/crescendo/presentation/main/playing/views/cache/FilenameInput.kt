package com.paranid5.crescendo.presentation.main.playing.views.cache

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.presentation.main.playing.PlayingViewModel
import com.paranid5.crescendo.presentation.main.playing.properties.compose.collectFilenameAsState
import com.paranid5.crescendo.presentation.ui.LocalAppColors
import com.paranid5.crescendo.presentation.ui.utils.DefaultOutlinedTextField

@Composable
fun FilenameInput(
    modifier: Modifier = Modifier,
    viewModel: PlayingViewModel = koinActivityViewModel()
) {
    val filename by viewModel.collectFilenameAsState()

    DefaultOutlinedTextField(
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