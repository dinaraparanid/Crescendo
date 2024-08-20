package com.paranid5.crescendo.feature.stream.fetch.presentation.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.feature.stream.fetch.view_model.FetchStreamState
import com.paranid5.crescendo.feature.stream.fetch.view_model.FetchStreamUiIntent
import com.paranid5.crescendo.feature.stream.fetch.view_model.FetchStreamUiIntent.Buttons

@Composable
internal fun ButtonsContainer(
    state: FetchStreamState,
    onUiIntent: (FetchStreamUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = Column(
    modifier = modifier.animateContentSize(),
    verticalArrangement = Arrangement.spacedBy(dimensions.padding.extraMedium),
    horizontalAlignment = Alignment.CenterHorizontally,
) {
    val buttonModifier = Modifier.fillMaxWidth()

    when {
        state.isUrlMangerVisible -> {
            UrlManagerButton(
                modifier = buttonModifier,
                title = stringResource(R.string.play),
                icon = ImageVector.vectorResource(R.drawable.ic_play_outlined),
                onClick = { onUiIntent(Buttons.PlayClick) },
            )

            if (state.isDownloadButtonVisible)
                UrlManagerButton(
                    modifier = buttonModifier,
                    title = stringResource(R.string.download),
                    icon = ImageVector.vectorResource(R.drawable.ic_download),
                    onClick = { onUiIntent(Buttons.DownloadClick) },
                )

            UrlManagerButton(
                modifier = buttonModifier,
                title = stringResource(R.string.next),
                icon = ImageVector.vectorResource(R.drawable.ic_next),
                onClick = { onUiIntent(Buttons.NextClick) },
            )
        }

        else -> ContinueButton(state = state, modifier = buttonModifier) {
            onUiIntent(Buttons.ContinueClick)
        }
    }
}
