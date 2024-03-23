package com.paranid5.crescendo.core.impl.trimmer

import android.os.Parcelable
import com.paranid5.crescendo.core.common.trimming.TrimRange
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrimRangeModel(
    val startPointMillis: Long = 0,
    val totalDurationMillis: Long = 0
) : Parcelable {
    constructor(entity: TrimRange) : this(
        startPointMillis = entity.startPointMillis,
        totalDurationMillis = entity.totalDurationMillis
    )
}

fun TrimRangeModel.toEntity() =
    TrimRange(startPointMillis, totalDurationMillis)
