package com.paranid5.crescendo.presentation.main.appbar

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors
import com.paranid5.crescendo.navigation.Screens
import com.paranid5.crescendo.ui.appbar.appBarHeight

@Composable
fun AppBar(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    BottomAppBar(
        containerColor = colors.primary,
        modifier = modifier
            .fillMaxWidth()
            .height(appBarHeight)
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        AppBarItem(
            title = stringResource(R.string.tracks),
            image = painterResource(R.drawable.tracks),
            screen = Screens.Tracks,
            modifier = Modifier
                .weight(1F)
                .padding(top = 12.dp)
        )

        AppBarItem(
            title = stringResource(R.string.track_collections),
            image = painterResource(R.drawable.playlist),
            screen = Screens.TrackCollections.Albums,
            modifier = Modifier
                .weight(1F)
                .padding(top = 12.dp)
        )

        AppBarItem(
            title = stringResource(R.string.streaming),
            image = painterResource(R.drawable.stream),
            screen = Screens.StreamFetching,
            modifier = Modifier
                .weight(1F)
                .padding(top = 12.dp)
        )

        AppBarItem(
            title = stringResource(R.string.favourites),
            image = Icons.Filled.Favorite,
            screen = Screens.Favourites,
            modifier = Modifier
                .weight(1F)
                .padding(top = 12.dp)
        )

        AppBarItem(
            title = stringResource(R.string.settings),
            image = Icons.Filled.Settings,
            screen = Screens.Settings,
            modifier = Modifier
                .weight(1F)
                .padding(top = 12.dp)
        )
    }
}