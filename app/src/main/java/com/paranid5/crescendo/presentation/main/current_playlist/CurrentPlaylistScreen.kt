package com.paranid5.crescendo.presentation.main.current_playlist

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.presentation.main.current_playlist.effects.TrackDismissEffect
import com.paranid5.crescendo.presentation.main.current_playlist.views.CurrentPlaylistBar
import com.paranid5.crescendo.presentation.main.current_playlist.views.CurrentPlaylistTrackList
import com.paranid5.crescendo.presentation.ui.theme.TransparentUtility

@Composable
fun CurrentPlaylistScreen(modifier: Modifier = Modifier) {
    TrackDismissEffect()

    Column(modifier) {
        CurrentPlaylistBar(
            Modifier
                .fillMaxWidth()
                .padding(
                    top = topPadding,
                    bottom = 8.dp,
                    start = 8.dp,
                    end = 8.dp,
                )
        )

        CurrentPlaylistBarSeparator(Modifier.height(2.dp))

        CurrentPlaylistTrackList(
            Modifier.padding(
                top = 16.dp,
                start = 8.dp,
                end = 4.dp,
                bottom = 8.dp
            )
        )
    }
}

@Composable
private fun CurrentPlaylistBarSeparator(modifier: Modifier = Modifier) =
    Spacer(
        modifier
            .fillMaxWidth()
            .background(TransparentUtility)
    )

private inline val topPadding
    @Composable
    get() = when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> 8.dp
        else -> 32.dp
    }