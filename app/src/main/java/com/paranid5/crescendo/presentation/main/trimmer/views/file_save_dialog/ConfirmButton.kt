package com.paranid5.crescendo.presentation.main.trimmer.views.file_save_dialog

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerUIHandler
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun ConfirmButton(
    isSaveButtonClickable: Boolean,
    track: com.paranid5.crescendo.core.common.tracks.Track,
    outputFilename: String,
    audioFormat: com.paranid5.crescendo.core.common.caching.Formats,
    trimRange: com.paranid5.crescendo.core.common.trimming.TrimRange,
    pitchAndSpeed: com.paranid5.crescendo.core.common.trimming.PitchAndSpeed,
    fadeDurations: com.paranid5.crescendo.core.common.trimming.FadeDurations,
    isDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    viewModel: TrimmerViewModel = koinActivityViewModel(),
    trimmerUIHandler: TrimmerUIHandler = koinInject()
) {
    val context = LocalContext.current
    val colors = LocalAppColors.current

    var isDialogShown by isDialogShownState

    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.backgroundAlternative
        ),
        onClick = {
            viewModel.viewModelScope.launch {
                trimmerUIHandler.trimTrackAndSendBroadcast(
                    context = context.applicationContext,
                    track = track,
                    outputFilename = outputFilename.trim(),
                    audioFormat = audioFormat,
                    trimRange = trimRange,
                    pitchAndSpeed = pitchAndSpeed,
                    fadeDurations = fadeDurations
                )
            }

            isDialogShown = false
        },
        enabled = isSaveButtonClickable,
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