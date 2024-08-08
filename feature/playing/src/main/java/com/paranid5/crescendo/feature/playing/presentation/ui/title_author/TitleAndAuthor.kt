package com.paranid5.crescendo.feature.playing.presentation.ui.title_author

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.feature.playing.presentation.ui.composition_local.LocalPalette
import com.paranid5.crescendo.feature.playing.view_model.PlayingState
import com.paranid5.crescendo.utils.extensions.getBrightDominantOrPrimary

@Composable
internal fun TitleAndAuthor(
    state: PlayingState,
    modifier: Modifier = Modifier,
    textAlignment: Alignment.Horizontal = Alignment.Start,
) {
    val paletteColor = LocalPalette.current.getBrightDominantOrPrimary()

    Column(modifier) {
        Title(
            textColor = paletteColor,
            state = state,
            modifier = Modifier.align(textAlignment),
        )

        Spacer(Modifier.height(dimensions.padding.small))

        Author(
            textColor = paletteColor,
            state = state,
            modifier = Modifier.align(textAlignment),
        )
    }
}
