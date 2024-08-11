package com.paranid5.crescendo.navigation

import com.paranid5.crescendo.core.common.navigation.Screen
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

internal sealed class AppScreen(override val title: String) : Screen {

    @Parcelize
    data object Play : AppScreen("play")

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
    data object Preferences : AppScreen(title = "preferences")
}
