package com.paranid5.crescendo.presentation.main

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.paranid5.crescendo.core.resources.ui.theme.CrescendoTheme
import com.paranid5.crescendo.navigation.LocalNavController
import com.paranid5.crescendo.navigation.NavHostController
import com.paranid5.crescendo.presentation.composition_locals.LocalActivity
import com.paranid5.crescendo.utils.extensions.getColorCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setNavigationBarColorToTransparent()

        setContent {
            CrescendoTheme {
                val mainNavController = NavHostController(rememberNavController())

                CompositionLocalProvider(
                    LocalNavController provides mainNavController,
                    LocalActivity provides this,
                ) {
                    App(Modifier.fillMaxSize())
                }
            }
        }
    }
}

private fun MainActivity.setNavigationBarColorToTransparent() {
    window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    window.navigationBarColor = getColorCompat(android.R.color.transparent)
}