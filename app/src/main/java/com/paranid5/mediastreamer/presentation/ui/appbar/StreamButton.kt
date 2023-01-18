package com.paranid5.mediastreamer.presentation.ui.appbar

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.presentation.composition_locals.LocalStreamState
import com.paranid5.mediastreamer.presentation.composition_locals.screen
import com.paranid5.mediastreamer.presentation.ui.screens.LocalNavController
import com.paranid5.mediastreamer.presentation.ui.theme.LocalAppColors

@Composable
fun StreamButton() {
    val navHostController = LocalNavController.current
    val streamingScreen = LocalStreamState.current.screen

    FloatingActionButton(onClick = { navHostController.navigateIfNotSame(streamingScreen) }) {
        Icon(
            painter = painterResource(R.drawable.stream),
            contentDescription = stringResource(id = R.string.home),
            tint = LocalAppColors.current.value.background,
            modifier = Modifier.width(30.dp).height(30.dp)
        )
    }
}