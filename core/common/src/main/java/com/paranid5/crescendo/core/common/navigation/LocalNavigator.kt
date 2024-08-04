package com.paranid5.crescendo.core.common.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

typealias NavigatorCompositionLocal = ProvidableCompositionLocal<Navigator<out Screen>?>

val LocalNavigator = staticCompositionLocalOf<Navigator<out Screen>?> { null }

@Composable
fun NavigatorCompositionLocal.requireCurrent() = requireNotNull(current)

@Suppress("UNCHECKED_CAST")
@Composable
fun <N : Navigator<out Screen>> NavigatorCompositionLocal.requireTyped() = requireCurrent() as N
