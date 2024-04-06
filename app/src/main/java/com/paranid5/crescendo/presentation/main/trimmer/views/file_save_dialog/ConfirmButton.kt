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
import com.paranid5.crescendo.core.common.caching.Formats
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.core.common.trimming.FadeDurations
import com.paranid5.crescendo.core.common.trimming.PitchAndSpeed
import com.paranid5.crescendo.core.common.trimming.TrimRange
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerUIHandler
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun ConfirmButton(
    isSaveButtonClickable: Boolean,
    track: Track,
    outputFilename: String,
    audioFormat: Formats,
    trimRange: TrimRange,
    pitchAndSpeed: PitchAndSpeed,
    fadeDurations: FadeDurations,
    isDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    viewModel: TrimmerViewModel = koinViewModel(),
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