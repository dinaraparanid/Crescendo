package com.paranid5.crescendo.playing.presentation.views.cache

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors

@Composable
internal fun CacheDialogLabel(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    Text(
        text = stringResource(R.string.cache_properties),
        modifier = modifier,
        color = colors.primary,
        maxLines = 1,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
}