package com.paranid5.mediastreamer.presentation

import androidx.navigation.NavHostController
import com.paranid5.mediastreamer.presentation.main_activity.MainActivityViewModel
import kotlinx.coroutines.flow.update

class NavHostController(
    val value: NavHostController? = null,
    private val mainActivityViewModel: MainActivityViewModel? = null
) {
    private inline val currentRouteState
        get() = mainActivityViewModel!!.curScreenState

    private inline val screensStackState
        get() = mainActivityViewModel!!.screensStack

    fun navigateIfNotSame(screen: Screens): Screens {
        val currentRoute = currentRouteState.value

        if (currentRoute != screen) {
            screensStackState.update { it.apply { add(currentRoute) } }
            currentRouteState.update { screen }
            value!!.navigate(screen.title)
            return screen
        }

        return currentRoute
    }

    /**
     * Pops screen stack, replacing root with deleted element.
     * If stack is empty, nothing happens.
     * @return null if stack is empty
     */

    fun onBackPressed() = screensStackState.value.removeLastOrNull()?.let { screen ->
        currentRouteState.update { screen }
        value!!.navigate(screen.title)
    }
}