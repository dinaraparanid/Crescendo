package com.paranid5.crescendo.feature.meta_editor.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.feature.meta_editor.view_model.MetaEditorViewModel
import com.paranid5.crescendo.feature.meta_editor.view_model.MetaEditorViewModelImpl
import org.koin.androidx.compose.koinViewModel

@Composable
fun MetaEditorScreen(
    trackPath: String,
    modifier: Modifier = Modifier,
    viewModel: MetaEditorViewModel = koinViewModel<MetaEditorViewModelImpl>(),
) {
    Box(modifier) {
        Text("TODO: Meta Editor Screen", Modifier.align(Alignment.Center))
    }
}
