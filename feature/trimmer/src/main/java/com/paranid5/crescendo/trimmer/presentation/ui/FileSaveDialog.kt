package com.paranid5.crescendo.trimmer.presentation.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.trimmer.presentation.ui.file_save_dialog.FileSaveDialogContent
import com.paranid5.crescendo.trimmer.view_model.TrimmerState.FileSaveDialogProperties
import com.paranid5.crescendo.trimmer.view_model.TrimmerUiIntent
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FileSaveDialog(
    state: FileSaveDialogProperties,
    onUiIntent: (TrimmerUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isDialogVisible)
        BasicAlertDialog(
            modifier = modifier,
            onDismissRequest = {
                onUiIntent(TrimmerUiIntent.FileSave.UpdateDialogVisibility(isVisible = false))
            },
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(dimensions.corners.medium),
                colors = CardDefaults.cardColors(containerColor = colors.background.primary),
            ) {
                FileSaveDialogContent(
                    state = state,
                    fileSaveOptions = rememberAudioFileSaveOptions(),
                    onUiIntent = onUiIntent,
                )
            }
        }
}

@Composable
private fun rememberAudioFileSaveOptions(): ImmutableList<String> {
    val mp3 = stringResource(R.string.mp3)
    val wav = stringResource(R.string.wav)
    val aac = stringResource(R.string.aac)

    return remember(mp3, wav, aac) {
        persistentListOf(mp3, wav, aac)
    }
}
