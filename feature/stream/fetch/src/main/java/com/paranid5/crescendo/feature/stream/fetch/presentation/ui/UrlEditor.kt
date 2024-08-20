package com.paranid5.crescendo.feature.stream.fetch.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.feature.stream.fetch.view_model.FetchStreamState
import com.paranid5.crescendo.feature.stream.fetch.view_model.FetchStreamUiIntent
import com.paranid5.crescendo.ui.foundation.AppOutlinedTextField
import com.paranid5.crescendo.ui.utils.clickableWithRipple
import com.paranid5.crescendo.utils.takeIfTrueOrNull

private val CancelIconSize = 24.dp

@Composable
internal fun UrlEditor(
    state: FetchStreamState,
    onUiIntent: (FetchStreamUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = Column(modifier) {
    Spacer(Modifier.height(dimensions.padding.extraMedium))

    FetchCardTitle(
        title = stringResource(R.string.stream_fetch_url_editor_title),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.padding.extraMedium),
    )

    Spacer(Modifier.height(dimensions.padding.extraMedium))

    UrlEditorTextField(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.padding.extraMedium),
    )

    Spacer(Modifier.height(dimensions.padding.extraMedium))
}

@Composable
private fun UrlEditorTextField(
    state: FetchStreamState,
    onUiIntent: (FetchStreamUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = AppOutlinedTextField(
    value = state.url,
    modifier = modifier,
    label = { Placeholder() },
    supportingText = state.isError.takeIfTrueOrNull { { ErrorLabel() } },
    trailingIcon = {
        AnimatedVisibility(visible = state.isCancelInputVisible) {
            CancelIcon { onUiIntent(FetchStreamUiIntent.UpdateUrl(url = "")) }
        }
    },
) {
    onUiIntent(FetchStreamUiIntent.UpdateUrl(url = it))
}

@Composable
private fun Placeholder(modifier: Modifier = Modifier) =
    Text(
        text = stringResource(R.string.stream_fetch_url_editor_placeholder),
        style = typography.caption,
        modifier = modifier,
    )

@Composable
private fun CancelIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) = Icon(
    imageVector = Icons.Default.Close,
    contentDescription = null,
    tint = colors.icon.selected,
    modifier = modifier
        .size(CancelIconSize)
        .clickableWithRipple(onClick = onClick),
)

@Composable
private fun ErrorLabel(modifier: Modifier = Modifier) =
    Text(
        text = stringResource(R.string.stream_fetch_url_editor_placeholder),
        color = colors.error,
        style = typography.captionSm,
        modifier = modifier,
    )
