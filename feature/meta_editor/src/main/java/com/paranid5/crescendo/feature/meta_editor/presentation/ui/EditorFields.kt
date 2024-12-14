package com.paranid5.crescendo.feature.meta_editor.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.feature.meta_editor.view_model.MetaEditorState
import com.paranid5.crescendo.feature.meta_editor.view_model.MetaEditorUiIntent
import com.paranid5.crescendo.ui.foundation.AppOutlinedTextField
import com.paranid5.crescendo.ui.foundation.AppText
import com.paranid5.crescendo.ui.foundation.LoadingBoxSize
import com.paranid5.crescendo.ui.foundation.isInitialOrLoading

private val BlockMinHeight = 437.dp

@Composable
internal fun EditorFields(
    state: MetaEditorState,
    onUiIntent: (MetaEditorUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = MetaEditorExpandableBlock(
    modifier = modifier,
    title = stringResource(R.string.meta_editor_meta_fields_title),
    icon = ImageVector.vectorResource(R.drawable.ic_edit),
    isLoading = state.trackPathUiState.isInitialOrLoading,
    size = LoadingBoxSize.FixedHeight(BlockMinHeight),
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensions.padding.medium),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.padding.medium),
    ) {
        AppOutlinedTextField(
            value = state.title,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            label = { InputHint(text = stringResource(R.string.title)) },
        ) {
            onUiIntent(MetaEditorUiIntent.Meta.UpdateTitle(title = it))
        }

        AppOutlinedTextField(
            value = state.artist,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            label = { InputHint(text = stringResource(R.string.artist)) },
        ) {
            onUiIntent(MetaEditorUiIntent.Meta.UpdateArtist(artist = it))
        }

        AppOutlinedTextField(
            value = state.album,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            label = { InputHint(text = stringResource(R.string.album)) },
        ) {
            onUiIntent(MetaEditorUiIntent.Meta.UpdateAlbum(album = it))
        }

        AppOutlinedTextField(
            value = state.numberInAlbum.toString(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
            ),
            label = { InputHint(text = stringResource(R.string.number_in_album)) },
        ) { input ->
            input.toIntOrNull()?.let {
                onUiIntent(MetaEditorUiIntent.Meta.UpdateNumberInAlbum(numberInAlbum = it))
            }
        }

        PathLabel(path = state.requireTrackPath())
    }
}

@Composable
private fun InputHint(text: String, modifier: Modifier = Modifier) = AppText(
    text = text,
    style = typography.caption,
    modifier = modifier,
)

@Composable
private fun PathLabel(path: String, modifier: Modifier = Modifier) =
    Column(modifier.fillMaxWidth()) {
        AppText(
            text = stringResource(R.string.meta_editor_meta_fields_path),
            style = typography.h.h3.copy(
                color = colors.text.primary,
                fontWeight = FontWeight.W700,
            ),
        )

        Spacer(Modifier.height(dimensions.padding.small))

        AppText(
            text = path,
            style = typography.regular.copy(
                color = colors.text.onHighContrast,
            ),
        )
    }
