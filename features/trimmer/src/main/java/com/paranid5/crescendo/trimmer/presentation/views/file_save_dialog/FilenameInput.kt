package com.paranid5.crescendo.trimmer.presentation.views.file_save_dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors
import com.paranid5.crescendo.ui.utils.DefaultOutlinedTextField

@Composable
internal fun FilenameInput(filenameState: MutableState<String>, modifier: Modifier = Modifier) {
    var filename by filenameState

    DefaultOutlinedTextField(
        value = filename,
        onValueChange = { filename = it },
        label = { FilenameLabel() },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    )
}

@Composable
private fun FilenameLabel(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    Text(
        text = stringResource(R.string.filename),
        color = colors.primary,
        fontSize = 12.sp,
        modifier = modifier
    )
}