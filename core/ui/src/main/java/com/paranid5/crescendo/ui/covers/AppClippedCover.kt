package com.paranid5.crescendo.ui.covers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import coil.compose.SubcomposeAsyncImage
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.ui.foundation.AppLoadingBox
import com.paranid5.crescendo.ui.foundation.AppLoadingBoxError
import com.paranid5.crescendo.ui.foundation.LoadingBoxSize
import com.paranid5.crescendo.ui.foundation.UiState
import com.paranid5.crescendo.ui.foundation.getOrNull

@Composable
fun AppClippedCover(
    coverUiState: UiState<ImageContainer>,
    modifier: Modifier = Modifier,
    coverShape: Shape = RoundedCornerShape(dimensions.corners.small),
    contentScale: ContentScale = ContentScale.FillHeight,
    errorBackgroundColor: Color = colors.background.highContrast,
    onRetry: (() -> Unit)? = null,
) {
    val context = LocalContext.current

    val coverRequest = remember(coverUiState, context) {
        coverUiState.getOrNull()?.let {
            coverModel(data = it.data, context = context)
        }
    }

    AppLoadingBox(
        isLoading = coverUiState is UiState.Loading,
        size = LoadingBoxSize.FillMaxSize,
        modifier = modifier.clip(coverShape),
    ) {
        SubcomposeAsyncImage(
            model = coverRequest,
            contentDescription = null,
            alignment = Alignment.Center,
            contentScale = contentScale,
            modifier = modifier
                .clip(coverShape)
                .border(
                    width = dimensions.borders.minimum,
                    color = colors.button.primary,
                    shape = coverShape,
                ),
            loading = {
                AppLoadingBox(
                    isLoading = true,
                    size = LoadingBoxSize.FillMaxSize,
                )
            },
            error = {
                AppLoadingBoxError(
                    onErrorButtonClick = onRetry,
                    errorText = stringResource(R.string.something_went_wrong),
                    errorButtonText = stringResource(R.string.retry),
                    modifier = Modifier
                        .fillMaxSize()
                        .background(errorBackgroundColor),
                )
            },
        )
    }
}
