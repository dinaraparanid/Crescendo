package com.paranid5.crescendo.navigation

import androidx.navigation.NavHostController
import arrow.core.raise.nullable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class Navigator(val navHost: NavHostController? = null) {
    private val _currentScreenState by lazy {
        MutableStateFlow<Screens>(Screens.Tracks)
    }

    val currentScreenState by lazy {
        _currentScreenState.asStateFlow()
    }

    fun updateCurrentScreen(screen: Screens) =
        _currentScreenState.update { screen }

    private val _screensStackState by lazy {
        MutableStateFlow(emptyList<Screens>())
    }

    private val screensStackState by lazy {
        _screensStackState.asStateFlow()
    }

    private fun updateScreensStack(screensStack: List<Screens>) =
        _screensStackState.update { screensStack }

    private inline val curScreen
        get() = _currentScreenState.value

    private inline val screensStack
        get() = screensStackState.value

    fun navigateIfNotSame(screen: Screens): Screens {
        val currentRoute = curScreen

        if (currentRoute == screen)
            return currentRoute

        updateScreensStack(screensStack + currentRoute)
        updateCurrentScreen(screen)
        navHost?.navigate(screen.title)
        return screen
    }

    /**
     * Pops screen stack, replacing root with deleted element.
     * If stack is empty, nothing happens.
     * @return new current route or null if stack was empty
     */

    fun onBackPressed() = nullable<Screens> {
        val newRoute = screensStack.lastOrNull().bind()
        val newScreensStack = screensStack.dropLast(1)

        updateScreensStack(newScreensStack)
        updateCurrentScreen(newRoute)
        navHost.bind().navigate(newRoute.title)

        return newRoute
    }
}