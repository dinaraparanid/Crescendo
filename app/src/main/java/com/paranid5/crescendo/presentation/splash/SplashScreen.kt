package com.paranid5.crescendo.presentation.splash

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.presentation.splash.views.SplashBackgroundImage
import com.paranid5.crescendo.presentation.splash.views.VersionLabels
import com.paranid5.crescendo.presentation.ui.LocalAppColors

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    Surface(
        modifier = modifier,
        color = colors.background
    ) {
        SplashBackgroundImage(Modifier.fillMaxWidth())

        Column(Modifier.fillMaxSize()) {
            Spacer(Modifier.weight(6F))
            VersionLabels(Modifier.align(Alignment.CenterHorizontally))
            Spacer(Modifier.weight(17F))
        }
    }
}