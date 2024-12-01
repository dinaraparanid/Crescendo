package com.paranid5.crescendo.feature.meta_editor.presentation.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.paranid5.crescendo.feature.meta_editor.view_model.MetaEditorUiIntent

@Composable
internal fun LoadMetaEffect(
    trackPath: String,
    onUiIntent: (MetaEditorUiIntent) -> Unit,
) = LaunchedEffect(Unit) {
    onUiIntent(MetaEditorUiIntent.Lifecycle.Create(trackPath = trackPath))
}
