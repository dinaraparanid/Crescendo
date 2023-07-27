package com.paranid5.mediastreamer.presentation.appbar

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.presentation.Screens
import com.paranid5.mediastreamer.presentation.ui.theme.LocalAppColors

@Composable
fun AppBar(modifier: Modifier = Modifier) {
    val orientation = LocalConfiguration.current.orientation

    BottomAppBar(
        modifier
            .fillMaxWidth()
            .height(
                when (orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> 60.dp
                    else -> 80.dp
                }
            )
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(color = LocalAppColors.current.value.primary)
    ) {
        AppBarItem(
            title = R.string.tracks,
            image = painterResource(R.drawable.tracks),
            screen = Screens.Tracks,
            modifier = Modifier.weight(1F)
        )

        AppBarItem(
            title = R.string.track_collections,
            image = painterResource(R.drawable.playlists),
            screen = Screens.TrackCollections.Albums,
            modifier = Modifier.weight(1F)
        )

        AppBarItem(
            title = R.string.streaming,
            image = painterResource(R.drawable.stream_icon),
            screen = Screens.StreamFetching,
            modifier = Modifier.weight(1F)
        )

        AppBarItem(
            title = R.string.favourites,
            image = Icons.Filled.Favorite,
            screen = Screens.Favourites,
            modifier = Modifier.weight(1F)
        )

        AppBarItem(
            title = R.string.settings,
            image = Icons.Filled.Settings,
            screen = Screens.Settings,
            modifier = Modifier.weight(1F)
        )
    }
}