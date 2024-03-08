package com.paranid5.crescendo.core.impl.trimming

import android.os.Parcelable
import com.paranid5.crescendo.core.common.trimming.TrimRange
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrimRangeModel(val startPointMillis: Long, val totalDurationMillis: Long) : Parcelable {
    constructor() : this(0, 0)

    constructor(entity: TrimRange) : this(
        startPointMillis = entity.startPointMillis,
        totalDurationMillis = entity.totalDurationMillis
    )
}

fun TrimRangeModel.toEntity() =
    TrimRange(startPointMillis, totalDurationMillis)
