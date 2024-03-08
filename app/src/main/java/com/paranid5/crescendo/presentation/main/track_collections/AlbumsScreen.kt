package com.paranid5.crescendo.presentation.main.track_collections

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.core.resources.R

@Composable
fun AlbumsScreen(modifier: Modifier = Modifier) {
    Text(text = stringResource(R.string.albums), modifier = modifier)
}