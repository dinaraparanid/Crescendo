package com.paranid5.crescendo.domain.trimming

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class PitchAndSpeed(val pitch: Float, val speed: Float) : Parcelable {
    constructor() : this(1F, 1F)
}
