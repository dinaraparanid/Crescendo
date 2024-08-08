package com.paranid5.crescendo.feature.playing.presentation.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.feature.playing.presentation.ui.composition_local.LocalPalette
import com.paranid5.crescendo.feature.playing.presentation.ui.kebab.KebabMenuButton
import com.paranid5.crescendo.feature.playing.presentation.ui.title_author.TitleAndAuthor
import com.paranid5.crescendo.feature.playing.view_model.PlayingState
import com.paranid5.crescendo.feature.playing.view_model.PlayingUiIntent
import com.paranid5.crescendo.utils.extensions.getBrightDominantOrPrimary

@Composable
internal fun TitleAndPropertiesButton(
    state: PlayingState,
    onUiIntent: (PlayingUiIntent) -> Unit,
    modifier: Modifier = Modifier,
    textAlignment: Alignment.Horizontal = Alignment.Start,
) = Row(modifier) {
    TitleAndAuthor(
        state = state,
        textAlignment = textAlignment,
        modifier = Modifier.weight(1F),
    )

    Spacer(Modifier.width(dimensions.padding.small))

    KebabMenuButton(
        tint = LocalPalette.current.getBrightDominantOrPrimary(),
        state = state,
        onUiIntent = onUiIntent,
    )
}
