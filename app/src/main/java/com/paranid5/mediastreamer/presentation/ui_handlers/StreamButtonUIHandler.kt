package com.paranid5.mediastreamer.presentation.ui_handlers

import com.paranid5.mediastreamer.presentation.ui.screens.NavHostController
import com.paranid5.mediastreamer.presentation.ui.screens.Screens

class StreamButtonUIHandler : UIHandler {
    fun navigateToStream(navHostController: NavHostController, currentScreenTitle: String) =
        navHostController.navigateIfNotSame(
            screen = when (currentScreenTitle) {
                Screens.StreamScreen.Searching.title -> Screens.StreamScreen.Streaming
                else -> Screens.StreamScreen.Searching
            }
        )
}