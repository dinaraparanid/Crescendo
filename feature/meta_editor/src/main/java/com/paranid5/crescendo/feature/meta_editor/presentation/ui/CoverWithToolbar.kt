package com.paranid5.crescendo.feature.meta_editor.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.feature.meta_editor.view_model.MetaEditorState
import com.paranid5.crescendo.feature.meta_editor.view_model.MetaEditorUiIntent

@Composable
internal fun CoverWithToolbar(
    state: MetaEditorState,
    onUiIntent: (MetaEditorUiIntent) -> Unit,
    modifier: Modifier = Modifier,
    toolbarModifier: Modifier = Modifier,
) = Box(
    modifier = modifier,
    contentAlignment = Alignment.TopCenter,
) {
    Toolbar(
        state = state,
        onUiIntent = onUiIntent,
        modifier = toolbarModifier
            .fillMaxWidth()
            .zIndex(1F)
            .padding(horizontal = dimensions.padding.extraMedium),
    )

    CurrentCover(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier.fillMaxWidth(),
    )
}