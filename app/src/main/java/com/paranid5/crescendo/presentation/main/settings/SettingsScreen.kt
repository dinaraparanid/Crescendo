package com.paranid5.crescendo.presentation.main.settings

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.R

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    Text(text = stringResource(id = R.string.settings), modifier = modifier)
}