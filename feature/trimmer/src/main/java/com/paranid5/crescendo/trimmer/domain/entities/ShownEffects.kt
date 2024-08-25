package com.paranid5.crescendo.trimmer.domain.entities

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
enum class ShownEffects : Parcelable { NONE, FADE, PITCH_SPEED }
