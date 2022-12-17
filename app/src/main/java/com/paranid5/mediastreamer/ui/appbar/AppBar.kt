package com.paranid5.mediastreamer.ui.appbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.ui.theme.LocalAppColors

@Composable
fun AppBar(modifier: Modifier = Modifier) {
    Row(modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
        .background(color = LocalAppColors.current.value.primary)
    ) {
        arrayOf(
            AppBarItemData(title = R.string.about_app) {},
            AppBarItemData(title = R.string.home) {},
            AppBarItemData(title = R.string.settings) {}
        ).forEach { (title, onClick) ->
            AppBarItem(title = title, onClick = onClick)
        }
    }
}