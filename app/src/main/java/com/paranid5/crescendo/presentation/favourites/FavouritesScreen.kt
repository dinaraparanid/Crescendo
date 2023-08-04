package com.paranid5.crescendo.presentation.favourites

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.R
import com.paranid5.crescendo.presentation.Screens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@Composable
fun FavouritesScreen(curScreenState: MutableStateFlow<Screens>) {
    curScreenState.update { Screens.Favourites }
    Text(text = stringResource(id = R.string.favourites))
}