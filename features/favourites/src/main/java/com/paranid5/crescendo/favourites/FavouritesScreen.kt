package com.paranid5.crescendo.favourites

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.core.resources.R

@Composable
fun FavouritesScreen(modifier: Modifier = Modifier) {
    Text(text = stringResource(id = R.string.favourites), modifier = modifier)
}