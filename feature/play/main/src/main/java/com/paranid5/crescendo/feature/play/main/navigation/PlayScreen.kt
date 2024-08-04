package com.paranid5.crescendo.feature.play.main.navigation

import com.paranid5.crescendo.core.common.navigation.Screen
import kotlinx.parcelize.Parcelize

internal sealed class PlayScreen(override val title: String) : Screen {

    @Parcelize
    data object Primary : PlayScreen("primary")

    @Parcelize
    data object Favourites : PlayScreen("favourites")

    @Parcelize
    data object Playlists : PlayScreen("playlists")

    @Parcelize
    data object Recent : PlayScreen("recent")
}
