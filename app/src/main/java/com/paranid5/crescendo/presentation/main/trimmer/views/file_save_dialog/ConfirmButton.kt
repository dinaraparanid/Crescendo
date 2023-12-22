package com.paranid5.crescendo.presentation.main.trimmer.views.file_save_dialog

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.R
import com.paranid5.crescendo.domain.caching.CacheTrimRange
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors

@Composable
fun ConfirmButton(
    isClickable: Boolean,
    trimRange: CacheTrimRange,
    trackPath: String,
    isDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAppColors.current
    var isDialogShown by isDialogShownState

    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.backgroundAlternative
        ),
        onClick = {
            // TODO: trim file
            isDialogShown = false
        },
        enabled = isClickable,
        content = { TrimLabel() }
    )
}

@Composable
private fun TrimLabel(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    Text(
        text = stringResource(R.string.trim),
        color = colors.fontColor,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}