package com.paranid5.crescendo.feature.meta_editor.presentation.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.feature.meta_editor.view_model.MetaEditorState
import com.paranid5.crescendo.feature.meta_editor.view_model.MetaEditorUiIntent
import com.paranid5.crescendo.ui.foundation.LoadingBoxSize
import com.paranid5.crescendo.ui.foundation.isError
import com.paranid5.crescendo.ui.foundation.isEvaluating
import com.paranid5.crescendo.ui.track.AppTrackList

private val BlockHeight = 427.dp

@Composable
internal fun SimilarTracksPicker(
    state: MetaEditorState,
    onUiIntent: (MetaEditorUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = MetaEditorExpandableBlock(
    modifier = modifier,
    title = stringResource(R.string.meta_editor_similar_tracks_title),
    icon = ImageVector.vectorResource(R.drawable.ic_similar),
    isLoading = state.similarTracksUiState.isEvaluating,
    isError = state.similarTracksUiState.isError,
    size = LoadingBoxSize.FixedHeight(BlockHeight),
    onRetryClick = { onUiIntent(MetaEditorUiIntent.Lifecycle.Refresh) },
) {
    val tracksStates by remember(state.similarTracks) {
        derivedStateOf { state.similarTracks }
    }

    AppTrackList(
        tracks = tracksStates,
        key = { _, track -> track.hashCode() },
        modifier = Modifier.fillMaxWidth().heightIn(max = BlockHeight),
        contentPadding = PaddingValues(bottom = dimensions.padding.small),
    ) { tracks, index, modifier ->
        val track = remember(tracks, index) { tracks[index] }

        SimilarTrackItem(
            trackUiState = tracks[index],
            modifier = modifier,
        ) {
            onUiIntent(MetaEditorUiIntent.Meta.SimilarTrackClicked(track = track))
        }
    }
}
