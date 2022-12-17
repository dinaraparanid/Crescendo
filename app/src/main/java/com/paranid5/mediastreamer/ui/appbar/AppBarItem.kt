package com.paranid5.mediastreamer.ui.appbar

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

data class AppBarItemData(@StringRes val title: Int, val onClick: () -> Unit)

@Composable
fun RowScope.AppBarItem(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    onClick: () -> Unit
) {
    IconButton(
        modifier = modifier.weight(1F),
        onClick = onClick
    ) {
        Text(text = stringResource(title))
    }
}