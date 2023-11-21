package com.paranid5.crescendo.presentation

import androidx.navigation.NavHostController
import com.paranid5.crescendo.presentation.main_activity.MainActivityViewModel

class NavHostController(
    val value: NavHostController? = null,
    private val mainActivityViewModel: MainActivityViewModel? = null
) {
    private inline val curScreen
        get() = mainActivityViewModel!!.curScreenState.value

    private fun setCurScreen(screen: Screens) =
        mainActivityViewModel!!.setCurScreen(screen)

    private inline val screensStack
        get() = mainActivityViewModel!!.screensStackState.value

    private fun setScreensStack(screensStack: MutableList<Screens>) =
        mainActivityViewModel!!.setScreensStack(screensStack)

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