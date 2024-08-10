package com.paranid5.crescendo.feature.play.main.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.common.navigation.LocalNavigator
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.feature.play.main.navigation.PlayScreen
import com.paranid5.crescendo.feature.play.main.navigation.requirePlayNavigator
import com.paranid5.crescendo.ui.utils.clickableWithRipple
import com.paranid5.crescendo.utils.extensions.simpleShadow

private val CardWidth = 148.dp
private val CardHeight = 128.dp
private val CardElevation = 4.dp
private val IconSize = 24.dp

@Composable
internal fun PlayScreenCards(modifier: Modifier = Modifier) {
    val navigator = LocalNavigator.requirePlayNavigator()
    val shape = RoundedCornerShape(dimensions.padding.medium)

    val cardModifier = Modifier
        .size(width = CardWidth, height = CardHeight)
        .simpleShadow(
            elevation = CardElevation,
            shape = shape,
        )
        .clip(shape)
        .background(colors.background.card)

    Row(modifier.horizontalScroll(rememberScrollState())) {
        Spacer(Modifier.width(dimensions.padding.extraMedium))

        PlayScreenCard(
            title = stringResource(R.string.play_favourites),
            icon = ImageVector.vectorResource(R.drawable.ic_heart),
            modifier = cardModifier.clickableWithRipple {
                navigator.replaceIfNotSame(PlayScreen.Favourites)
            },
        )

        Spacer(Modifier.width(dimensions.padding.extraMedium))

        PlayScreenCard(
            title = stringResource(R.string.play_playlists),
            icon = ImageVector.vectorResource(R.drawable.ic_playlist),
            modifier = cardModifier.clickableWithRipple {
                navigator.replaceIfNotSame(PlayScreen.Playlists)
            },
        )

        Spacer(Modifier.width(dimensions.padding.extraMedium))

        PlayScreenCard(
            title = stringResource(R.string.play_recent),
            icon = ImageVector.vectorResource(R.drawable.ic_recent),
            modifier = cardModifier.clickableWithRipple {
                navigator.replaceIfNotSame(PlayScreen.Recent)
            },
        )

        Spacer(Modifier.width(dimensions.padding.extraMedium))
    }
}

@Composable
private fun PlayScreenCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) = Box(modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = dimensions.padding.medium)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(IconSize),
            tint = colors.text.onCard,
        )

        Spacer(Modifier.width(dimensions.padding.extraSmall))

        Text(
            text = title,
            style = typography.h.h3,
            color = colors.text.onCard,
        )
    }
}
