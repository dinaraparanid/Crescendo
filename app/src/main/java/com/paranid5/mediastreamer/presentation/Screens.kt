package com.paranid5.mediastreamer.presentation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class Screens(val title: String) : Parcelable {

    sealed class MainScreens(title: String) : Screens("main/$title") {

        @Parcelize
        object Searching : MainScreens("searching")

        sealed class StreamScreens(title: String) : MainScreens("stream/$title") {
            @Parcelize
            object Streaming : MainScreens("streaming")

            @Parcelize
            object AudioEffects : MainScreens("audio_effects")
        }
    }

    @Parcelize
    object AboutApp : Screens("about_app")

    @Parcelize
    object Favourites : Screens(title = "favourites")

    @Parcelize
    object Settings : Screens(title = "settings")
}