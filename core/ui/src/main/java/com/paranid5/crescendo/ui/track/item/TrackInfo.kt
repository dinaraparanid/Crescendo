package com.paranid5.crescendo.ui.track.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.ui.track.ui_state.TrackUiState
import com.paranid5.crescendo.utils.extensions.artistAlbum

@Composable
fun TrackInfo(
    track: TrackUiState,
    textColor: Color,
    modifier: Modifier = Modifier
) = Column(modifier) {
    TrackTitle(
        modifier = Modifier.align(Alignment.Start),
        trackTitle = track.title,
        textColor = textColor,
    )

    Spacer(Modifier.height(dimensions.padding.extraSmall))

    TrackArtistAlbum(
        modifier = Modifier.align(Alignment.Start),
        trackArtistAlbum = track.artistAlbum,
        textColor = textColor,
    )
}
