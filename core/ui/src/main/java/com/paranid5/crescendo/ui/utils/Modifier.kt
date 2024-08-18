package com.paranid5.crescendo.ui.utils

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import kotlinx.collections.immutable.persistentListOf

private const val WIDTH_OF_SHADOW_BRUSH = 200F
private const val SHIMMER_DURATION_MS = 1500

@Composable
fun Modifier.shimmerEffect(
    accentColor: Color = colors.background.alternative,
    basicColor: Color = colors.background.highContrast,
    widthOfShadowBrush: Float = WIDTH_OF_SHADOW_BRUSH,
    durationMillis: Int = SHIMMER_DURATION_MS,
): Modifier {
    val transition = rememberInfiniteTransition(label = "shimmer_transition")

    val translateAnimation by transition.animateFloat(
        initialValue = 0F,
        targetValue = durationMillis + widthOfShadowBrush,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_animation"
    )

    val shimmerColors = remember(basicColor) {
        persistentListOf(
            basicColor.copy(alpha = .6F),
            basicColor.copy(alpha = .4F),
            basicColor.copy(alpha = .2F),
            accentColor.copy(alpha = .2F),
            basicColor.copy(alpha = .2F),
            basicColor.copy(alpha = .4F),
            basicColor.copy(alpha = .6F),
        )
    }

    return this
        .background(color = accentColor)
        .background(
            brush = Brush.linearGradient(
                colors = shimmerColors,
                start = Offset(x = translateAnimation - widthOfShadowBrush, y = 0F),
                end = Offset(x = translateAnimation, y = widthOfShadowBrush),
            ),
        )
}

inline fun Modifier.applyIf(
    condition: Boolean,
    block: Modifier.() -> Modifier
): Modifier = if (condition) block() else this

inline fun <T : Any> Modifier.applyIfNotNull(
    instance: T?,
    block: Modifier.(T) -> Modifier
): Modifier = if (instance != null) block(instance) else this

@Composable
inline fun Modifier.applyComposableIf(
    condition: Boolean,
    block: @Composable Modifier.() -> Modifier
): Modifier = if (condition) block() else this

@Composable
inline fun <T : Any> Modifier.applyComposableIfNotNull(
    instance: T?,
    block: @Composable Modifier.(T) -> Modifier
): Modifier = if (instance != null) block(instance) else this
