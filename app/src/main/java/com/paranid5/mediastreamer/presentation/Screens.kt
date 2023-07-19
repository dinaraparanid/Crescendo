package com.paranid5.mediastreamer.presentation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class Screens(val title: String) : Parcelable {

    @Parcelize
    object Tracks : Screens("tracks")

    sealed class TrackCollections(title: String) : Screens("track_collections/$title") {
        @Parcelize
        object Albums : TrackCollections("albums")

        @Parcelize
        object CustomPlaylists : TrackCollections("custom_playlists")
    }

    @Parcelize
    object Artists : Screens("artists")

    @Parcelize
    object Searching : Screens("searching")

    sealed class Stream(title: String) : Screens("stream/$title") {
        @Parcelize
        object Streaming : Stream("streaming")

        @Parcelize
        object AudioEffects : Stream("audio_effects")
    }

    @Parcelize
    object AboutApp : Screens("about_app")

    @Parcelize
    object Favourites : Screens(title = "favourites")

    @Parcelize
    object Settings : Screens(title = "settings")
}