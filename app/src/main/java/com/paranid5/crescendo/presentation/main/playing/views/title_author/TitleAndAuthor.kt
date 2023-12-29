package com.paranid5.crescendo.presentation.main.playing.views.title_author

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.domain.media.AudioStatus
import com.paranid5.crescendo.presentation.main.playing.PlayingViewModel
import com.paranid5.crescendo.presentation.main.playing.views.title_author.Author
import com.paranid5.crescendo.presentation.main.playing.views.title_author.Title
import com.paranid5.crescendo.presentation.ui.extensions.getLightMutedOrPrimary

@Composable
fun TitleAndAuthor(
    viewModel: PlayingViewModel,
    audioStatus: AudioStatus,
    palette: Palette?,
    modifier: Modifier = Modifier,
    textAlignment: Alignment.Horizontal = Alignment.Start,
) {
    val paletteColor = palette.getLightMutedOrPrimary()

    Column(modifier) {
        Title(
            viewModel = viewModel,
            audioStatus = audioStatus,
            paletteColor = paletteColor,
            modifier = Modifier.align(textAlignment)
        )

        Spacer(Modifier.height(5.dp))

        Author(
            viewModel = viewModel,
            audioStatus = audioStatus,
            paletteColor = paletteColor,
            modifier = Modifier.align(textAlignment)
        )
    }
}