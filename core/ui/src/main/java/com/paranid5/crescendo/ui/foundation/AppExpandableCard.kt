package com.paranid5.crescendo.ui.foundation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.ui.utils.clickableWithRipple

private val IconSize = 16.dp

private const val ExpandedAngle = 180F
private const val CollapsedAngle = 0F

@Composable
fun AppExpandableCard(
    title: String,
    modifier: Modifier = Modifier,
    isInitiallyExpanded: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    var isExpanded by remember { mutableStateOf(isInitiallyExpanded) }
    val shape = RoundedCornerShape(dimensions.corners.small)

    Column(
        modifier = modifier
            .clip(shape)
            .background(colors.background.highContrast)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(shape)
                .clickableWithRipple(bounded = true) { isExpanded = isExpanded.not() }
        ) {
            Spacer(Modifier.height(dimensions.padding.extraMedium))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(Modifier.width(dimensions.padding.extraMedium))

                Text(
                    text = title,
                    color = colors.text.primary,
                    style = typography.h.h3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1F)
                )

                Spacer(Modifier.width(dimensions.padding.small))

                ExpandableIcon(
                    isExpanded = isExpanded,
                    contentDescription = title,
                )

                Spacer(Modifier.width(dimensions.padding.extraMedium))
            }

            Spacer(Modifier.height(dimensions.padding.extraMedium))
        }

        AnimatedVisibility(
            visible = isExpanded,
            modifier = Modifier.padding(horizontal = dimensions.padding.extraMedium),
        ) {
            Column(Modifier.fillMaxWidth()) {
                content()
                Spacer(Modifier.height(dimensions.padding.extraMedium))
            }
        }
    }
}

@Composable
private fun ExpandableIcon(
    isExpanded: Boolean,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) ExpandedAngle else CollapsedAngle,
        label = "",
    )

    Icon(
        imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_down),
        contentDescription = contentDescription,
        tint = colors.text.primary,
        modifier = modifier
            .size(IconSize)
            .graphicsLayer(rotationZ = rotation),
    )
}
