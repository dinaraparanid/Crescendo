package com.paranid5.mediastreamer.presentation

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class NavHostController(
    @JvmField val value: NavHostController? = null,
    initialRoute: String = Screens.StreamScreen.Searching.title
) {
    @JvmField
    val currentRouteState = MutableStateFlow(initialRoute)
    private val screensStack = mutableListOf<String>()

    fun navigateIfNotSame(screen: Screens) {
        val route = screen.title
        val currentRoute = currentRouteState.value

        if (currentRoute != route) {
            screensStack.add(currentRoute)
            currentRouteState.update { route }
            value!!.navigate(route)
        }
    }

    fun onBackPressed() = screensStack.removeLastOrNull()?.let { screen ->
        currentRouteState.update { screen }
        value!!.navigate(screen)
    }
}

@JvmField
val LocalNavController = staticCompositionLocalOf { NavHostController() }