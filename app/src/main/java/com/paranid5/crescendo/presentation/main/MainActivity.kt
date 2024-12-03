package com.paranid5.crescendo.presentation.main

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.paranid5.crescendo.core.common.navigation.LocalNavigator
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme
import com.paranid5.crescendo.navigation.AppNavigator
import com.paranid5.crescendo.utils.extensions.getColorCompat
import com.paranid5.crescendo.view_model.MainViewModelImpl
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val viewModel by viewModel<MainViewModelImpl>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setNavigationBarColorToTransparent()

        setContent {
            AppTheme {
                val navHost = rememberNavController()
                val mainNavController = remember(navHost) { AppNavigator(navHost) }

                CompositionLocalProvider(LocalNavigator provides mainNavController) {
                    App(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

private fun MainActivity.setNavigationBarColorToTransparent() = window.run {
    setFlags(
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        isNavigationBarContrastEnforced = false
    } else {
        @Suppress("DEPRECATION")
        navigationBarColor = getColorCompat(android.R.color.transparent)
    }
}