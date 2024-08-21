package com.paranid5.crescendo.feature.stream.fetch.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.feature.stream.fetch.view_model.FetchStreamUiIntent
import com.paranid5.crescendo.ui.covers.AppClippedCover
import com.paranid5.crescendo.ui.covers.ImageContainer
import com.paranid5.crescendo.ui.foundation.UiState
import com.paranid5.crescendo.ui.metadata.VideoMetadataUiState
import com.paranid5.crescendo.utils.extensions.timeString

private val ImageHeight = 164.dp

@Composable
internal fun MetaContainer(
    videoMeta: VideoMetadataUiState,
    coverUiState: UiState<ImageContainer>,
    onUiIntent: (FetchStreamUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = Column(modifier) {
    Spacer(Modifier.height(dimensions.padding.extraMedium))

    FetchCardTitle(
        title = stringResource(R.string.stream_fetch_url_meta_title),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.padding.extraMedium),
    )

    Spacer(Modifier.height(dimensions.padding.extraMedium))

    AppClippedCover(
        coverUiState = coverUiState,
        onRetry = { onUiIntent(FetchStreamUiIntent.Retry.RefreshCover) },
        modifier = Modifier
            .height(ImageHeight)
            .align(Alignment.CenterHorizontally)
            .padding(horizontal = dimensions.padding.extraMedium),
    )

    Spacer(Modifier.height(dimensions.padding.extraMedium))

    TitleWithDuration(
        videoMeta = videoMeta,
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.CenterHorizontally),
    )

    Spacer(Modifier.height(dimensions.padding.extraMedium))
}

@Composable
private fun TitleWithDuration(
    videoMeta: VideoMetadataUiState,
    modifier: Modifier = Modifier,
) = Column(modifier = modifier) {
    Text(
        text = videoMeta.title,
        textAlign = TextAlign.Center,
        color = colors.text.onHighContrast,
        style = typography.h.h3,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.padding.extraMedium),
    )

    Spacer(Modifier.height(dimensions.padding.small))

    Text(
        text = stringResource(
            R.string.stream_fetch_url_meta_duration,
            videoMeta.durationMillis.timeString,
        ),
        textAlign = TextAlign.Center,
        color = colors.text.onHighContrast,
        style = typography.h.h3,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.padding.extraMedium),
    )
}
