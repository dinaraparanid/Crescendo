package com.paranid5.crescendo.core.resources.ui.theme

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.drawscope.ContentDrawScope

internal object NoRipple : Indication, IndicationInstance {
    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource) = this

    override fun ContentDrawScope.drawIndication() = drawContent()
}
