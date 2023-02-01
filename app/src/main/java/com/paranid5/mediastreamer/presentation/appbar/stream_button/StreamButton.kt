package com.paranid5.mediastreamer.presentation.appbar.stream_button

import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.presentation.LocalNavController
import com.paranid5.mediastreamer.presentation.Screens
import com.paranid5.mediastreamer.presentation.ui.theme.LocalAppColors
import org.koin.androidx.compose.get

@Composable
fun StreamButton(
    modifier: Modifier = Modifier,
    streamButtonUIHandler: StreamButtonUIHandler = get()
) {
    val navHostController = LocalNavController.current
    val currentScreenTitle by navHostController.currentRouteState.collectAsState()

    FloatingActionButton(
        modifier = modifier,
        onClick = { streamButtonUIHandler.navigateToStream(navHostController, currentScreenTitle) }
    ) {
        Icon(
            painter = painterResource(
                id = when (currentScreenTitle) {
                    Screens.StreamScreen.Streaming.title -> R.drawable.search_icon
                    else -> R.drawable.stream_icon
                }
            ),
            contentDescription = stringResource(id = R.string.home),
            tint = LocalAppColors.current.value.primary,
            modifier = Modifier.size(30.dp)
        )
    }
}