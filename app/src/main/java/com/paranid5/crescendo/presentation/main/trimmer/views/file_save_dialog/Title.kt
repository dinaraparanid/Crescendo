package com.paranid5.crescendo.presentation.main.trimmer.views.file_save_dialog

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.R
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors

@Composable
fun Title(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    Text(
        text = stringResource(R.string.save_as),
        modifier = modifier.padding(vertical = 15.dp),
        color = colors.primary,
        maxLines = 1,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
}