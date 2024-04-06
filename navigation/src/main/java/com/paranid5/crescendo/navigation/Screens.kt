package com.paranid5.crescendo.navigation

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

sealed class Screens(val title: String) : Parcelable {

    @Parcelize
    data object Tracks : Screens("tracks")

    sealed class TrackCollections(title: String) : Screens("track_collections/$title") {
        @Parcelize
        data object Albums : TrackCollections("albums")

        @Parcelize
        data object CustomPlaylists : TrackCollections("custom_playlists")
    }

    @Parcelize
    data object Artists : Screens("artists")

    @Parcelize
    data object StreamFetching : Screens("you_tube_fetching")

    sealed class Audio(title: String) : Screens("audio/$title") {
        @Parcelize
        data object AudioEffects : Audio("audio_effects")

        @Parcelize
        data class Trimmer(val trackPath: String) : Audio("trimmer/$trackPath") {
            @Parcelize
            companion object : Audio("trimmer/{trackPath}") {
                @IgnoredOnParcel
                const val TRACK_PATH_KEY = "trackPath"
            }
        }
    }

    @Parcelize
    data object AboutApp : Screens("about_app")

    @Parcelize
    data object Favourites : Screens(title = "favourites")

    @Parcelize
    data object Settings : Screens(title = "settings")
}