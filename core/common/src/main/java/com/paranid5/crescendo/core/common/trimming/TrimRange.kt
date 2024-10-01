package com.paranid5.crescendo.core.common.trimming

import kotlinx.serialization.Serializable

@Serializable
data class TrimRange(val startPointMillis: Long, val totalDurationMillis: Long) {
    constructor() : this(0, 0)
}

val TrimRange.startPointSecs
    get() = startPointMillis / 1000

val TrimRange.totalDurationSecs
    get() = totalDurationMillis / 1000
