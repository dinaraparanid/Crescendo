package com.paranid5.crescendo.cache.presentation.view

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.cache.domain.CacheInteractor
import com.paranid5.crescendo.cache.presentation.CacheViewModel
import com.paranid5.crescendo.cache.presentation.composition_local.LocalDownloadUrl
import com.paranid5.crescendo.cache.presentation.properties.collectCacheFormatAsState
import com.paranid5.crescendo.cache.presentation.properties.collectFilenameAsState
import com.paranid5.crescendo.cache.presentation.properties.collectIsCacheButtonClickableAsState
import com.paranid5.crescendo.cache.presentation.properties.collectTrimRangeAsState
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
internal fun ConfirmButton(
    isDialogShownState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    viewModel: CacheViewModel = koinViewModel(),
    interactor: CacheInteractor = koinInject()
) {
    val downloadingUrl = LocalDownloadUrl.current

    val isButtonClickable by viewModel.collectIsCacheButtonClickableAsState()
    val filename by viewModel.collectFilenameAsState()
    val format by viewModel.collectCacheFormatAsState()
    val trimRange by viewModel.collectTrimRangeAsState()

    val coroutineScope = rememberCoroutineScope()
    var isDialogShown by isDialogShownState

    Button(
        modifier = modifier,
        enabled = isButtonClickable,
        colors = ButtonDefaults.buttonColors(containerColor = colors.background.alternative),
        content = { ConfirmButtonLabel() },
        onClick = {
            coroutineScope.launch {
                interactor.startCaching(
                    url = downloadingUrl,
                    desiredFilename = filename,
                    format = format,
                    trimRange = trimRange,
                    viewModel = viewModel,
                )

                isDialogShown = false
            }
        },
    )
}

@Composable
private fun ConfirmButtonLabel(modifier: Modifier = Modifier) =
    Text(
        text = stringResource(R.string.start_caching),
        color = colors.text.primary,
        style = typography.regular,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier,
    )