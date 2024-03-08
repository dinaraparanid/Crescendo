package com.paranid5.crescendo.core.common.trimming

data class FadeDurations(val fadeInSecs: Long, val fadeOutSecs: Long) {
    constructor() : this(0, 0)
}
