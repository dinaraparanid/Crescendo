package com.paranid5.crescendo.feature.stream.fetch.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.feature.stream.fetch.view_model.FetchStreamState
import com.paranid5.crescendo.feature.stream.fetch.view_model.FetchStreamUiIntent
import com.paranid5.crescendo.ui.foundation.AppLoadingBox
import com.paranid5.crescendo.ui.foundation.UiState
import com.paranid5.crescendo.utils.doNothing
import com.paranid5.crescendo.utils.extensions.simpleShadow

private val FetchCardElevation = 4.dp
private val FetchCardMinHeight = 128.dp

@Composable
internal fun FetchCard(
    state: FetchStreamState,
    onUiIntent: (FetchStreamUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(AppTheme.dimensions.corners.small)

    AppLoadingBox(
        isLoading = state.videoMetadataUiState is UiState.Loading,
        minHeight = FetchCardMinHeight,
        modifier = modifier
            .simpleShadow(
                elevation = FetchCardElevation,
                shape = shape,
            )
            .clip(shape)
    ) {
        when {
            state.isUrlMangerVisible -> doNothing

            else -> UrlEditor(
                state = state,
                onUiIntent = onUiIntent,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.background.highContrast),
            )
        }
    }
}