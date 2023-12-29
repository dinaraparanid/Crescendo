package com.paranid5.crescendo.presentation.main.playing.views.cache

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.R
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import com.paranid5.crescendo.presentation.ui.utils.DefaultOutlinedTextField

@Composable
fun FilenameInput(filenameState: MutableState<String>, modifier: Modifier = Modifier) {
    var filename by filenameState

    DefaultOutlinedTextField(
        value = filename,
        onValueChange = { filename = it },
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