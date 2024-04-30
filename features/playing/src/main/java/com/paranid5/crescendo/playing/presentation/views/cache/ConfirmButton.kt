package com.paranid5.crescendo.playing.presentation.views.cache

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
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors
import com.paranid5.crescendo.playing.domain.PlayingInteractor
import com.paranid5.crescendo.playing.presentation.PlayingViewModel
import com.paranid5.crescendo.playing.presentation.properties.compose.collectCacheFormatAsState
import com.paranid5.crescendo.playing.presentation.properties.compose.collectCurrentUrlAsState
import com.paranid5.crescendo.playing.presentation.properties.compose.collectFilenameAsState
import com.paranid5.crescendo.playing.presentation.properties.compose.collectIsCacheButtonClickableAsState
import com.paranid5.crescendo.playing.presentation.properties.compose.collectTrimRangeAsState
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
internal fun ConfirmButton(
    isDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    viewModel: PlayingViewModel = koinViewModel(),
    interactor: PlayingInteractor = koinInject()
) {
    val colors = LocalAppColors.current
    var isDialogShown by isDialogShownState

    val isButtonClickable by viewModel.collectIsCacheButtonClickableAsState()
    val url by viewModel.collectCurrentUrlAsState()
    val filename by viewModel.collectFilenameAsState()
    val format by viewModel.collectCacheFormatAsState()
    val trimRange by viewModel.collectTrimRangeAsState()

    Button(
        modifier = modifier,
        enabled = isButtonClickable,
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.backgroundAlternative
        ),
        content = { ConfirmButtonLabel() },
        onClick = {
            interactor.launchVideoCacheService(
                url = url,
                desiredFilename = filename,
                format = format,
                trimRange = trimRange
            )

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