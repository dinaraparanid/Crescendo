package com.paranid5.crescendo.presentation.settings

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.R
import com.paranid5.crescendo.presentation.Screens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@Composable
fun SettingsScreen(curScreenState: MutableStateFlow<Screens>) {
    curScreenState.update { Screens.Settings }
    Text(text = stringResource(id = R.string.settings))
}