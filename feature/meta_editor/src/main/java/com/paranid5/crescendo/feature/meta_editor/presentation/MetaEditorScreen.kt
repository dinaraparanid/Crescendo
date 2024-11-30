package com.paranid5.crescendo.feature.meta_editor.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.feature.meta_editor.presentation.effect.LoadMetaEffect
import com.paranid5.crescendo.feature.meta_editor.presentation.ui.Header
import com.paranid5.crescendo.feature.meta_editor.view_model.MetaEditorViewModel
import com.paranid5.crescendo.feature.meta_editor.view_model.MetaEditorViewModelImpl
import com.paranid5.crescendo.utils.extensions.collectLatestAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun MetaEditorScreen(
    trackPath: String,
    modifier: Modifier = Modifier,
    viewModel: MetaEditorViewModel = koinViewModel<MetaEditorViewModelImpl>(),
) {
    val state by viewModel.stateFlow.collectLatestAsStateWithLifecycle()
    val onUiIntent = viewModel::onUiIntent

    LoadMetaEffect(trackPath = trackPath, onUiIntent = onUiIntent)

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        val horizontalPaddingModifier = Modifier.padding(horizontal = dimensions.padding.medium)

        ContentSpacer()

        Header(state = state, onUiIntent = onUiIntent, modifier = horizontalPaddingModifier)

        ContentSpacer()
    }
}

@Composable
private fun ContentSpacer() = Spacer(Modifier.padding(dimensions.padding.medium))
