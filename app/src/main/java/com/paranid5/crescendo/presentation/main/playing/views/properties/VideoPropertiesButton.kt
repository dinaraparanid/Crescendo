package com.paranid5.crescendo.presentation.main.playing.views.properties

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.R

/** TODO: Video properties */

@Composable
fun VideoPropertiesButton(tint: Color, modifier: Modifier = Modifier) =
    IconButton(
        modifier = modifier,
        onClick = { /** TODO: Video properties */ }
    ) {
        PropertiesIcon(
            tint = tint,
            modifier = Modifier
                .height(50.dp)
                .width(25.dp),
        )
    }

@Composable
private fun PropertiesIcon(tint: Color, modifier: Modifier = Modifier) =
    Icon(
        painter = painterResource(R.drawable.three_dots),
        contentDescription = stringResource(R.string.settings),
        tint = tint,
        modifier = modifier
    )