package com.paranid5.crescendo.core.impl.navigation

import androidx.navigation.NavHostController
import arrow.core.raise.nullable
import com.paranid5.crescendo.core.common.navigation.Navigator
import com.paranid5.crescendo.core.common.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NavigatorImpl<S : Screen>(
    initialScreen: S,
    override val navHost: NavHostController? = null,
) : Navigator<S> {

    private val _currentScreenState by lazy {
        MutableStateFlow<S>(initialScreen)
    }

    override val currentScreenState by lazy {
        _currentScreenState.asStateFlow()
    }

    override fun updateCurrentScreen(screen: S) =
        _currentScreenState.update { screen }

    private val _screensStackState by lazy {
        MutableStateFlow(emptyList<S>())
    }

    override val screensStackState by lazy {
        _screensStackState.asStateFlow()
    }

    override fun updateScreensStack(stack: List<S>) =
        _screensStackState.update { stack }

    private inline val curScreen
        get() = _currentScreenState.value

    private inline val screensStack
        get() = screensStackState.value

    override fun navigateIfNotSame(screen: S): S {
        val currentRoute = curScreen

        if (currentRoute == screen)
            return currentRoute

        _screensStackState.update { it + currentRoute }
        updateCurrentScreen(screen)
        navHost?.navigate(screen.title)
        return screen
    }

    override fun onBackPressed() = nullable<S> {
        val newRoute = screensStack.lastOrNull().bind()
        _screensStackState.update { it.dropLast(1) }
        updateCurrentScreen(newRoute)
        navHost.bind().navigate(newRoute.title)
        return newRoute
    }
}
