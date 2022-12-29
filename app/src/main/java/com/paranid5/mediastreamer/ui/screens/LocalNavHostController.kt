package com.paranid5.mediastreamer.ui.screens

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController

data class NavHostController(
    @JvmField val value: NavHostController? = null,
    @JvmField var currentRouteState: String = Screens.StreamScreen.Searching.title
) {
    fun navigateIfNotSame(screens: Screens) {
        val route = screens.title

        if (currentRouteState != route) {
            currentRouteState = route
            value!!.navigate(route)
        }
    }
}

@JvmField
val LocalNavController = staticCompositionLocalOf { NavHostController() }