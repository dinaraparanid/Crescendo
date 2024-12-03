package com.paranid5.crescendo.feature.meta_editor.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.feature.meta_editor.presentation.effect.LoadMetaEffect
import com.paranid5.crescendo.feature.meta_editor.presentation.ui.CoverWithToolbar
import com.paranid5.crescendo.feature.meta_editor.presentation.ui.CoversPicker
import com.paranid5.crescendo.feature.meta_editor.presentation.ui.EditorFields
import com.paranid5.crescendo.feature.meta_editor.presentation.ui.Header
import com.paranid5.crescendo.feature.meta_editor.view_model.MetaEditorViewModel
import com.paranid5.crescendo.feature.meta_editor.view_model.MetaEditorViewModelImpl
import com.paranid5.crescendo.utils.extensions.collectLatestAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun MetaEditorScreen(
    trackPath: String,
    modifier: Modifier = Modifier,
    safeDrawingModifier: Modifier = Modifier,
    viewModel: MetaEditorViewModel = koinViewModel<MetaEditorViewModelImpl>(),
) {
    val state by viewModel.stateFlow.collectLatestAsStateWithLifecycle()
    val onUiIntent = viewModel::onUiIntent

    LoadMetaEffect(trackPath = trackPath, onUiIntent = onUiIntent)

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        val blockModifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.padding.medium)
            .clip(RoundedCornerShape(dimensions.corners.small))
            .background(colors.background.highContrast)

        CoverWithToolbar(
            state = state,
            onUiIntent = onUiIntent,
            modifier = Modifier.fillMaxWidth(),
            toolbarModifier = safeDrawingModifier.fillMaxWidth(),
        )

        ContentSpacer()

        Header(state = state, onUiIntent = onUiIntent, modifier = blockModifier)

        ContentSpacer()

        CoversPicker(state = state, onUiIntent = onUiIntent, modifier = blockModifier)

        ContentSpacer()

        EditorFields(state = state, onUiIntent = onUiIntent, modifier = blockModifier)

        ContentSpacer()
    }
}

@Composable
private fun ContentSpacer() = Spacer(Modifier.padding(dimensions.padding.medium))
