package com.paranid5.crescendo.core.common.trimming

import kotlinx.serialization.Serializable

@Serializable
data class PitchAndSpeed(val pitch: Float, val speed: Float) {
    constructor() : this(1F, 1F)
}
