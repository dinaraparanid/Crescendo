package com.paranid5.crescendo.navigation

import com.paranid5.crescendo.core.common.navigation.Screen
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

internal sealed class AppScreen(override val title: String) : Screen {

    @Parcelize
    data object Play : AppScreen("play")

    sealed class TrackCollections(title: String) : AppScreen("track_collections/$title") {
        @Parcelize
        data object Albums : TrackCollections("albums")

        @Parcelize
        data object CustomPlaylists : TrackCollections("custom_playlists")
    }

    @Parcelize
    data object Artists : AppScreen("artists")

    @Parcelize
    data object StreamFetching : AppScreen("stream_fetching")

    sealed class Audio(title: String) : AppScreen("audio/$title") {
        @Parcelize
        data object AudioEffects : Audio("audio_effects")

        @Parcelize
        data class Trimmer(val trackPath: String) : Audio("trimmer/$trackPath") {
            @Parcelize
            companion object : Audio("trimmer/{trackPath}") {
                @IgnoredOnParcel
                const val TrackPathKey = "trackPath"
            }
        }
    }

    @Parcelize
    data object AboutApp : AppScreen("about_app")

    @Parcelize
    data object Favourites : AppScreen(title = "favourites")

    @Parcelize
    data object Settings : AppScreen(title = "settings")
}
