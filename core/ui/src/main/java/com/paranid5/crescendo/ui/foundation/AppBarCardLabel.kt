package com.paranid5.crescendo.ui.foundation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.utils.extensions.simpleShadow

private val CardLabelElevation = 4.dp

@Composable
fun AppBarCardLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(dimensions.padding.extraMedium)

    Box(
        modifier = Modifier
            .simpleShadow(
                elevation = CardLabelElevation,
                shape = shape,
            )
            .clip(shape)
            .background(colors.background.highContrast)
            .then(modifier)
    ) {
        Text(
            text = text,
            color = colors.text.primary,
            style = AppTheme.typography.body,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(dimensions.padding.small),
        )
    }
}
