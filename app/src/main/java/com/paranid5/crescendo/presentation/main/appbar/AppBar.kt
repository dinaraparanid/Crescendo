package com.paranid5.crescendo.presentation.main.appbar

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.navigation.Screens
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors

@Composable
fun AppBar(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    BottomAppBar(
        containerColor = colors.primary,
        modifier = modifier
            .fillMaxWidth()
            .height(appBarHeight)
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    ) {
        AppBarItem(
            title = stringResource(R.string.tracks),
            image = painterResource(R.drawable.tracks),
            screen = com.paranid5.crescendo.navigation.Screens.Tracks,
            modifier = Modifier
                .weight(1F)
                .padding(top = 12.dp)
        )

        AppBarItem(
            title = stringResource(R.string.track_collections),
            image = painterResource(R.drawable.playlists),
            screen = com.paranid5.crescendo.navigation.Screens.TrackCollections.Albums,
            modifier = Modifier
                .weight(1F)
                .padding(top = 12.dp)
        )

        AppBarItem(
            title = stringResource(R.string.streaming),
            image = painterResource(R.drawable.stream_icon),
            screen = com.paranid5.crescendo.navigation.Screens.StreamFetching,
            modifier = Modifier
                .weight(1F)
                .padding(top = 12.dp)
        )

        AppBarItem(
            title = stringResource(R.string.favourites),
            image = Icons.Filled.Favorite,
            screen = com.paranid5.crescendo.navigation.Screens.Favourites,
            modifier = Modifier
                .weight(1F)
                .padding(top = 12.dp)
        )

        AppBarItem(
            title = stringResource(R.string.settings),
            image = Icons.Filled.Settings,
            screen = com.paranid5.crescendo.navigation.Screens.Settings,
            modifier = Modifier
                .weight(1F)
                .padding(top = 12.dp)
        )
    }
}

inline val appBarHeight
    @Composable
    get() = when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> 80.dp
        else -> 110.dp
    }