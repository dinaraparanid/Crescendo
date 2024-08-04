package com.paranid5.crescendo.feature.play.main.navigation

import androidx.compose.runtime.Immutable
import androidx.navigation.NavHostController
import com.paranid5.crescendo.core.common.navigation.Navigator
import com.paranid5.crescendo.core.impl.navigation.NavigatorImpl

@Immutable
internal class PlayNavigator(
    navHost: NavHostController? = null,
) : Navigator<PlayScreen> by NavigatorImpl(
    initialScreen = PlayScreen.Primary,
    navHost = navHost,
)
