package com.paranid5.mediastreamer.presentation

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import com.paranid5.mediastreamer.presentation.Screens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class NavHostController(
    @JvmField val value: NavHostController? = null,
    initialRoute: String = Screens.StreamScreen.Searching.title
) {
    @JvmField
    val currentRouteState = MutableStateFlow(initialRoute)

    fun navigateIfNotSame(screen: Screens) {
        val route = screen.title

        if (currentRouteState.value != route) {
            currentRouteState.update { route }
            value!!.navigate(route)
        }
    }
}

@JvmField
val LocalNavController = staticCompositionLocalOf { NavHostController() }