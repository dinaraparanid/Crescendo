package com.paranid5.mediastreamer.presentation.appbar.stream_button

import com.paranid5.mediastreamer.presentation.NavHostController
import com.paranid5.mediastreamer.presentation.Screens
import com.paranid5.mediastreamer.presentation.UIHandler

class StreamButtonUIHandler : UIHandler {
    fun navigateToStream(navHostController: NavHostController, currentScreenTitle: String) =
        navHostController.navigateIfNotSame(
            screen = when (currentScreenTitle) {
                Screens.StreamScreen.Searching.title -> Screens.StreamScreen.Streaming
                else -> Screens.StreamScreen.Searching
            }
        )
}