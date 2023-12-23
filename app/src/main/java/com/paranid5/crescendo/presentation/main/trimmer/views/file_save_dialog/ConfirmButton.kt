package com.paranid5.crescendo.presentation.main.trimmer.views.file_save_dialog

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.R
import com.paranid5.crescendo.domain.caching.Formats
import com.paranid5.crescendo.domain.trimming.FadeDurations
import com.paranid5.crescendo.domain.trimming.TrimRange
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerUIHandler
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun ConfirmButton(
    isClickable: Boolean,
    trackPath: String,
    filenameState: State<String>,
    audioFormat: Formats,
    trimRange: TrimRange,
    fadeDurations: FadeDurations,
    isDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    trimmerUIHandler: TrimmerUIHandler = koinInject()
) {
    val colors = LocalAppColors.current
    var isDialogShown by isDialogShownState
    val outputFilename by filenameState
    val coroutineScope = rememberCoroutineScope()

    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.backgroundAlternative
        ),
        onClick = {
            coroutineScope.launch {
                trimmerUIHandler.trimTrack(
                    trackPath = trackPath,
                    outputFilename = outputFilename,
                    audioFormat = audioFormat,
                    trimRange = trimRange,
                    fadeDurations = fadeDurations
                )
            }

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