package com.paranid5.mediastreamer.presentation.ui.screens

sealed class Screens(@JvmField val title: String) {
    sealed class StreamScreen(title: String) : Screens("stream/$title") {
        object Searching : StreamScreen("searching")
        object Streaming : StreamScreen("streaming")
    }

    object AboutApp : Screens("about_app")
    object Favourite : Screens(title = "favourites")
    object Settings : Screens(title = "settings")
}