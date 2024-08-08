package com.paranid5.crescendo.feature.playing.presentation.ui.utils_buttons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.utils.extensions.simpleShadow

private val IconSize = 32.dp

@Composable
internal fun RepeatButton(
    isRepeating: Boolean,
    tint: Color,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) = Box(modifier) {
    IconButton(
        enabled = enabled,
        onClick = onClick,
        modifier = Modifier
            .simpleShadow(color = tint)
            .align(Alignment.Center),
    ) {
        RepeatIcon(
            isRepeating = isRepeating,
            tint = tint,
            modifier = Modifier.size(IconSize),
        )
    }
}

@Composable
private fun RepeatIcon(
    isRepeating: Boolean,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    val icon = remember(isRepeating) {
        when {
            isRepeating -> R.drawable.repeat
            else -> R.drawable.no_repeat
        }
    }

    Icon(
        modifier = modifier,
        imageVector = ImageVector.vectorResource(icon),
        contentDescription = stringResource(R.string.change_repeat),
        tint = tint,
    )
}
