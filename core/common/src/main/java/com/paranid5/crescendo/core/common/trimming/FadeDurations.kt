package com.paranid5.crescendo.core.common.trimming

import kotlinx.serialization.Serializable

@Serializable
data class FadeDurations(val fadeInSecs: Long, val fadeOutSecs: Long) {
    constructor() : this(0, 0)
}
