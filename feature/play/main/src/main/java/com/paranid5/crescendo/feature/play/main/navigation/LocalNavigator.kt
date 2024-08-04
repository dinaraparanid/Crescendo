package com.paranid5.crescendo.feature.play.main.navigation

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.core.common.navigation.NavigatorCompositionLocal
import com.paranid5.crescendo.core.common.navigation.requireTyped

@Composable
internal fun NavigatorCompositionLocal.requirePlayNavigator() =
    requireTyped<PlayNavigator>()
