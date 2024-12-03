package com.paranid5.crescendo.feature.meta_editor.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.paranid5.crescendo.feature.meta_editor.view_model.MetaEditorState
import com.paranid5.crescendo.feature.meta_editor.view_model.MetaEditorUiIntent
import com.paranid5.crescendo.ui.covers.AppClippedCover

@Composable
internal fun CurrentCover(
    state: MetaEditorState,
    onUiIntent: (MetaEditorUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = AppClippedCover(
    coverUiState = state.coverUiState,
    modifier = modifier,
    contentScale = ContentScale.Crop,
    onRetry = { onUiIntent(MetaEditorUiIntent.Lifecycle.Refresh) },
)
