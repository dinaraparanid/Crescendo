package com.paranid5.crescendo.trimmer.presentation.ui.file_save_dialog

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.paranid5.crescendo.core.common.caching.Formats
import com.paranid5.crescendo.core.common.trimming.FadeDurations
import com.paranid5.crescendo.core.common.trimming.PitchAndSpeed
import com.paranid5.crescendo.core.common.trimming.TrimRange
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.trimmer.domain.trimTrackAndSendBroadcast
import com.paranid5.crescendo.ui.track.ui_state.TrackUiState
import kotlinx.coroutines.launch

@Composable
internal fun ConfirmButton(
    isSaveButtonClickable: Boolean,
    track: TrackUiState,
    outputFilename: String,
    audioFormat: Formats,
    trimRange: TrimRange,
    pitchAndSpeed: PitchAndSpeed,
    fadeDurations: FadeDurations,
    isDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var isDialogShown by isDialogShownState
    val coroutineScope = rememberCoroutineScope()

    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.background.alternative
        ),
        onClick = {
            coroutineScope.launch {
                trimTrackAndSendBroadcast(
                    context = context.applicationContext,
                    track = track,
                    outputFilename = outputFilename.trim(),
                    audioFormat = audioFormat,
                    trimRange = trimRange,
                    pitchAndSpeed = pitchAndSpeed,
                    fadeDurations = fadeDurations,
                )
            }

            isDialogShown = false
        },
        enabled = isSaveButtonClickable,
        content = { TrimLabel() },
    )
}

@Composable
private fun TrimLabel(modifier: Modifier = Modifier) =
    Text(
        text = stringResource(R.string.trim),
        color = colors.text.primary,
        style = typography.regular,
        fontWeight = FontWeight.Bold,
        modifier = modifier,
    )
