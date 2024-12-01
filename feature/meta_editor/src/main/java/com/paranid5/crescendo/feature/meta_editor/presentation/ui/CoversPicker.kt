package com.paranid5.crescendo.feature.meta_editor.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.feature.meta_editor.view_model.MetaEditorState
import com.paranid5.crescendo.feature.meta_editor.view_model.MetaEditorUiIntent
import com.paranid5.crescendo.ui.covers.AppClippedCover
import com.paranid5.crescendo.ui.foundation.LoadingBoxSize
import com.paranid5.crescendo.ui.foundation.getOrNull
import com.paranid5.crescendo.ui.foundation.isError
import com.paranid5.crescendo.ui.foundation.isEvaluating
import com.paranid5.crescendo.ui.foundation.toUiState
import com.paranid5.crescendo.ui.utils.clickableWithRipple
import com.paranid5.crescendo.utils.extensions.orNil

private val BlockMinHeight = 148.dp
private val ImageSize = 80.dp

@Composable
internal fun CoversPicker(
    state: MetaEditorState,
    onUiIntent: (MetaEditorUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = MetaEditorExpandableBlock(
    modifier = modifier,
    title = stringResource(R.string.meta_editor_cover_picker_title),
    icon = ImageVector.vectorResource(R.drawable.ic_thumbnail),
    isLoading = state.similarCoversUiState.isEvaluating,
    size = LoadingBoxSize.FixedHeight(BlockMinHeight),
    isError = state.similarCoversUiState.isError,
    onRetryClick = { onUiIntent(MetaEditorUiIntent.Lifecycle.Refresh) },
) {
    val covers = remember(state.similarCoversUiState) {
        state.similarCoversUiState.getOrNull().orNil()
    }

    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensions.padding.medium),
        contentPadding = PaddingValues(horizontal = dimensions.padding.medium),
    ) {
        items(covers) { cover ->
            AppClippedCover(
                coverUiState = cover.toUiState(),
                modifier = Modifier
                    .size(ImageSize)
                    .clickableWithRipple(bounded = true) {
                        onUiIntent(MetaEditorUiIntent.Meta.SimilarCoverClicked(cover))
                    },
            )
        }
    }
}