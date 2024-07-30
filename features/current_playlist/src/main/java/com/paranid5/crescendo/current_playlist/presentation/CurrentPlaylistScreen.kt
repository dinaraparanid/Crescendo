package com.paranid5.crescendo.current_playlist.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.current_playlist.presentation.effects.TrackDismissEffect
import com.paranid5.crescendo.current_playlist.presentation.views.CurrentPlaylistBar
import com.paranid5.crescendo.current_playlist.presentation.views.CurrentPlaylistTrackList

@Composable
fun CurrentPlaylistScreen(modifier: Modifier = Modifier) {
    TrackDismissEffect()

    Column(modifier) {
        CurrentPlaylistBar(
            Modifier
                .fillMaxWidth()
                .padding(
                    top = topPadding,
                    bottom = dimensions.padding.small,
                    start = dimensions.padding.small,
                    end = dimensions.padding.small,
                )
        )

        CurrentPlaylistBarSeparator()

        CurrentPlaylistTrackList(
            Modifier.padding(
                top = dimensions.padding.extraMedium,
                start = dimensions.padding.small,
                end = dimensions.padding.extraSmall,
                bottom = dimensions.padding.small,
            )
        )
    }
}

@Composable
private fun CurrentPlaylistBarSeparator(modifier: Modifier = Modifier) =
    HorizontalDivider(
        modifier = modifier.fillMaxWidth(),
        thickness = dimensions.padding.minimum,
        color = colors.utils.transparentUtility,
    )

private inline val topPadding
    @Composable
    get() = when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> dimensions.padding.small
        else -> dimensions.padding.large
    }