package com.paranid5.mediastreamer.presentation.appbar.stream_button

import android.util.Log
import com.paranid5.mediastreamer.presentation.NavHostController
import com.paranid5.mediastreamer.presentation.StreamStates
import com.paranid5.mediastreamer.presentation.UIHandler
import com.paranid5.mediastreamer.presentation.screen

class StreamButtonUIHandler : UIHandler {
    fun navigateToStream(navHostController: NavHostController, nextStreamState: StreamStates) {
        Log.d("NEXT SCREEN", "$nextStreamState")
        navHostController.navigateIfNotSame(screen = nextStreamState.screen)
    }
}