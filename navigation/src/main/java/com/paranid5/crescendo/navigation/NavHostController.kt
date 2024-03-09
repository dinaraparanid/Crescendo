package com.paranid5.crescendo.navigation

import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NavHostController(
    val value: NavHostController? = null,
) {
    private val _curScreenState by lazy {
        MutableStateFlow<Screens>(Screens.Tracks)
    }

    val curScreenState by lazy { _curScreenState.asStateFlow() }

    fun setCurScreen(screen: Screens) =
        _curScreenState.update { screen }

    private val _screensStackState by lazy {
        MutableStateFlow(mutableListOf<Screens>())
    }

    val screensStackState by lazy { _screensStackState.asStateFlow() }

    private fun setScreensStack(screensStack: MutableList<Screens>) =
        _screensStackState.update { screensStack }

    private inline val curScreen
        get() = curScreenState.value

    private inline val screensStack
        get() = screensStackState.value

    fun navigateIfNotSame(screen: Screens): Screens {
        val currentRoute = curScreen

        if (currentRoute != screen) {
            setScreensStack(screensStack.apply { add(currentRoute) })
            setCurScreen(screen)
            value!!.navigate(screen.title)
            return screen
        }

        return currentRoute
    }

    /**
     * Pops screen stack, replacing root with deleted element.
     * If stack is empty, nothing happens.
     * @return new current route or null if stack was empty
     */

    fun onBackPressed(): Screens? {
        val newScreensStack = screensStack
        val newRoute = newScreensStack.removeLastOrNull() ?: return null

        setScreensStack(newScreensStack)
        setCurScreen(newRoute)
        value!!.navigate(newRoute.title)

        return newRoute
    }
}