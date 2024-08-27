package com.paranid5.crescendo.trimmer.presentation.ui.file_save_dialog

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.paranid5.crescendo.core.common.caching.Formats
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.trimmer.view_model.TrimmerUiIntent
import com.paranid5.crescendo.ui.foundation.AppRippleButton

@Composable
internal fun ConfirmButton(
    onUiIntent: (TrimmerUiIntent) -> Unit,
    isSaveButtonClickable: Boolean,
    outputFilename: String,
    audioFormat: Formats,
    isDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
) {
    var isDialogShown by isDialogShownState

    AppRippleButton(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.button.onBackgroundPrimary,
            contentColor = colors.text.onBackgroundPrimary,
            disabledContainerColor = colors.button.onBackgroundPrimaryDisabled,
            disabledContentColor = colors.text.tertiriary,
        ),
        onClick = {
            onUiIntent(
                TrimmerUiIntent.TrimTrack(
                    outputFilename = outputFilename.trim(),
                    audioFormat = audioFormat,
                )
            )

            isDialogShown = false
        },
        isEnabled = isSaveButtonClickable,
        content = { TrimLabel() },
    )
}

@Composable
private fun TrimLabel(modifier: Modifier = Modifier) =
    Text(
        text = stringResource(R.string.trim),
        style = typography.regular,
        fontWeight = FontWeight.Bold,
        modifier = modifier,
    )
