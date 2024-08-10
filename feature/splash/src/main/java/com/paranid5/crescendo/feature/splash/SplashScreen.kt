package com.paranid5.crescendo.feature.splash

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.feature.splash.ui.SplashBackgroundImage
import com.paranid5.crescendo.feature.splash.ui.VersionLabels

private const val TOP_PADDING_RATIO = 6F
private const val BOTTOM_PADDING_RATIO = 17F

@Composable
internal fun SplashScreen(modifier: Modifier = Modifier) =
    Surface(
        modifier = modifier,
        color = colors.background.primary,
    ) {
        SplashBackgroundImage(Modifier.fillMaxWidth())

        Column(Modifier.fillMaxSize()) {
            Spacer(Modifier.weight(TOP_PADDING_RATIO))
            VersionLabels(Modifier.align(Alignment.CenterHorizontally))
            Spacer(Modifier.weight(BOTTOM_PADDING_RATIO))
        }
    }