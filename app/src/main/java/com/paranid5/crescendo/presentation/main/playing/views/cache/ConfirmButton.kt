package com.paranid5.crescendo.presentation.main.playing.views.cache

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
import com.paranid5.crescendo.domain.caching.Formats
import com.paranid5.crescendo.domain.trimming.TrimRange
import com.paranid5.crescendo.presentation.main.playing.PlayingUIHandler
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import org.koin.compose.koinInject

@Composable
fun ConfirmButton(
    format: Formats,
    trimRange: TrimRange,
    isButtonClickable: Boolean,
    filename: String,
    isDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    playingUIHandler: PlayingUIHandler = koinInject()
) {
    val colors = LocalAppColors.current
    var isDialogShown by isDialogShownState

    Button(
        modifier = modifier,
        enabled = isButtonClickable,
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.backgroundAlternative
        ),
        content = { ConfirmButtonLabel() },
        onClick = {
            playingUIHandler.launchVideoCashService(filename, format, trimRange)
            isDialogShown = false
        },
    )
}

@Composable
private fun ConfirmButtonLabel(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    Text(
        text = stringResource(R.string.start_caching),
        color = colors.fontColor,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}