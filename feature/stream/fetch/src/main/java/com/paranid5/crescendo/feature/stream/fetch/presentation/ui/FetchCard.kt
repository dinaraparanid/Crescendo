package com.paranid5.crescendo.feature.stream.fetch.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.feature.stream.fetch.view_model.FetchStreamState
import com.paranid5.crescendo.feature.stream.fetch.view_model.FetchStreamUiIntent
import com.paranid5.crescendo.ui.foundation.AppLoadingBox
import com.paranid5.crescendo.ui.foundation.AppText
import com.paranid5.crescendo.ui.foundation.LoadingBoxSize
import com.paranid5.crescendo.ui.foundation.getOrNull
import com.paranid5.crescendo.ui.foundation.isError
import com.paranid5.crescendo.ui.foundation.isLoading
import com.paranid5.crescendo.utils.extensions.simpleShadow

private val FetchCardElevation = 4.dp
private val FetchCardMinHeight = 128.dp

@Composable
internal fun FetchCard(
    state: FetchStreamState,
    onUiIntent: (FetchStreamUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(dimensions.corners.small)

    AppLoadingBox(
        isLoading = state.videoMetadataUiState.isLoading,
        isError = state.videoMetadataUiState.isError,
        onErrorButtonClick = { onUiIntent(FetchStreamUiIntent.Retry.ClearMetaOnFailure) },
        size = LoadingBoxSize.FixedHeight(FetchCardMinHeight),
        modifier = modifier
            .simpleShadow(elevation = FetchCardElevation, shape = shape)
            .clip(shape)
    ) {
        val commonModifier = Modifier
            .fillMaxWidth()
            .background(colors.background.highContrast)

        when {
            state.isUrlMangerVisible -> state.videoMetadataUiState.getOrNull()?.let { videoMeta ->
                MetaContainer(
                    videoMeta = videoMeta,
                    coverUiState = state.coverUiState,
                    onUiIntent = onUiIntent,
                    modifier = commonModifier,
                )
            }

            else -> UrlEditor(
                state = state,
                onUiIntent = onUiIntent,
                modifier = commonModifier,
            )
        }
    }
}

@Composable
internal fun FetchCardTitle(
    title: String,
    modifier: Modifier = Modifier,
) = AppText(
    text = title,
    style = typography.h.h4.copy(color = colors.text.primary),
    modifier = modifier,
)
