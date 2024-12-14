package com.paranid5.crescendo.feature.meta_editor.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.feature.meta_editor.presentation.ui.model.SimilarTrackUiState
import com.paranid5.crescendo.ui.covers.AppClippedCover
import com.paranid5.crescendo.ui.foundation.AppText
import com.paranid5.crescendo.ui.utils.clickableWithRipple

private val ImageSize = 48.dp

@Composable
internal fun SimilarTrackItem(
    trackUiState: SimilarTrackUiState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) = Row(
    modifier = modifier
        .clip(RoundedCornerShape(dimensions.padding.extraMedium))
        .clickableWithRipple(bounded = true, onClick = onClick),
) {
    Spacer(Modifier.width(dimensions.padding.small))

    AppClippedCover(
        coverUiState = trackUiState.primaryCover,
        coverShape = RoundedCornerShape(dimensions.corners.extraSmall),
        modifier = Modifier.size(ImageSize),
    )

    Spacer(Modifier.width(dimensions.padding.medium))

    TrackInfo(
        trackUiState = trackUiState,
        modifier = Modifier.weight(1F),
    )

    Spacer(Modifier.width(dimensions.padding.small))
}

@Composable
private fun TrackInfo(
    trackUiState: SimilarTrackUiState,
    modifier: Modifier = Modifier,
) = Column(modifier) {
    AppText(
        text = trackUiState.title,
        maxLines = 1,
        style = typography.h.h4.copy(
            color = colors.text.primary,
            fontWeight = FontWeight.W600,
        ),
    )

    Spacer(Modifier.height(dimensions.padding.small))

    AppText(
        text = trackUiState.artists,
        maxLines = 1,
        style = typography.regular.copy(
            color = colors.text.primary,
            fontWeight = FontWeight.W600,
        ),
    )
}