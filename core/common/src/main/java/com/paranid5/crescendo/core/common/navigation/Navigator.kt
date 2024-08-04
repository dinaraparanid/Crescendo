package com.paranid5.crescendo.core.common.navigation

import androidx.compose.runtime.Immutable
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.StateFlow

@Immutable
interface Navigator<S : Screen> {
    val navHost: NavHostController?

    val currentScreenState: StateFlow<S>

    fun updateCurrentScreen(screen: S)

    val screensStackState: StateFlow<List<S>>

    fun updateScreensStack(stack: List<S>)

    fun pushIfNotSame(screen: S)

    fun replaceIfNotSame(screen: S)

    /**
     * Pops screen stack, replacing root with deleted element.
     * If stack is empty, nothing happens.
     * @return new current route or null if stack was empty
     */

    fun onBackPressed(): S?
}
