package com.paranid5.crescendo.feature.meta_editor.presentation.ui

import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.feature.meta_editor.view_model.MetaEditorState
import com.paranid5.crescendo.feature.meta_editor.view_model.MetaEditorUiIntent
import com.paranid5.crescendo.ui.covers.AppClippedCover

private val MinHeight = 300.dp

@Composable
internal fun CurrentCover(
    state: MetaEditorState,
    onUiIntent: (MetaEditorUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = AppClippedCover(
    coverUiState = state.coverUiState,
    modifier = modifier.heightIn(min = MinHeight),
    contentScale = ContentScale.Crop,
    onRetry = { onUiIntent(MetaEditorUiIntent.Lifecycle.Refresh) },
)
