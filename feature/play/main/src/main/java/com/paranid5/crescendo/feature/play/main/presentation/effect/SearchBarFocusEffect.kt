package com.paranid5.crescendo.feature.play.main.presentation.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalFocusManager
import com.paranid5.crescendo.feature.play.main.view_model.PlayState

@Composable
internal fun SearchBarFocusEffect(state: PlayState) {
    val focusManager = LocalFocusManager.current

    LaunchedEffect(state.isSearchActive) {
        if (state.isSearchActive.not())
            focusManager.clearFocus()
    }
}
