package com.paranid5.crescendo.feature.meta_editor.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.feature.meta_editor.view_model.MetaEditorState
import com.paranid5.crescendo.feature.meta_editor.view_model.MetaEditorUiIntent
import com.paranid5.crescendo.ui.foundation.AppLoadingBox
import com.paranid5.crescendo.ui.foundation.AppText
import com.paranid5.crescendo.ui.foundation.LoadingBoxSize
import com.paranid5.crescendo.ui.foundation.isInitialOrLoading
import com.paranid5.crescendo.ui.utils.clickableWithRipple

private val BlockMinHeight = 83.dp
private const val TitleMaxLines = 2
private const val ArtistMaxLines = 1

@Composable
internal fun Header(
    state: MetaEditorState,
    onUiIntent: (MetaEditorUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = AppLoadingBox(
    isLoading = state.trackPathUiState.isInitialOrLoading,
    size = LoadingBoxSize.FixedHeight(BlockMinHeight),
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.width(dimensions.padding.medium))

        TitleWithArtist(
            title = state.title,
            artist = state.artist,
            modifier = Modifier
                .padding(vertical = dimensions.padding.extraMedium)
                .weight(1F),
        )

        Spacer(Modifier.width(dimensions.padding.small))

        KebabButton(modifier = Modifier.size(dimensions.iconSize.big)) {
            onUiIntent(MetaEditorUiIntent.General.KebabClick)
        }

        Spacer(Modifier.width(dimensions.padding.medium))
    }
}

@Composable
private fun TitleWithArtist(
    title: String,
    artist: String,
    modifier: Modifier = Modifier,
) = Column(modifier) {
    AppText(
        text = title,
        maxLines = TitleMaxLines,
        overflow = TextOverflow.Ellipsis,
        style = typography.h.h3.copy(
            color = colors.text.primary,
            fontWeight = FontWeight.W700,
        ),
    )

    Spacer(Modifier.height(dimensions.padding.small))

    AppText(
        text = artist,
        maxLines = ArtistMaxLines,
        overflow = TextOverflow.Ellipsis,
        style = typography.body.copy(
            color = colors.text.primary,
            fontWeight = FontWeight.W700,
        ),
    )
}

@Composable
private fun KebabButton(modifier: Modifier = Modifier, onClick: () -> Unit) =
    Icon(
        imageVector = ImageVector.vectorResource(R.drawable.ic_kebab_menu),
        contentDescription = null,
        tint = colors.icon.primary,
        modifier = modifier.clickableWithRipple(onClick = onClick),
    )
