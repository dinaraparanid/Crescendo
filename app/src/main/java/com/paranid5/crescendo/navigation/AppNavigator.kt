package com.paranid5.crescendo.navigation

import androidx.compose.runtime.Immutable
import androidx.navigation.NavHostController
import com.paranid5.crescendo.core.common.navigation.Navigator
import com.paranid5.crescendo.core.impl.navigation.NavigatorImpl

@Immutable
internal class AppNavigator(
    navHost: NavHostController? = null,
) : Navigator<AppScreen> by NavigatorImpl(
    initialScreen = AppScreen.Play,
    navHost = navHost,
)
