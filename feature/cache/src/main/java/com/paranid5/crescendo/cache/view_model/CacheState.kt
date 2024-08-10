package com.paranid5.crescendo.cache.view_model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.paranid5.crescendo.core.common.caching.Formats
import com.paranid5.crescendo.core.common.trimming.TrimRange
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class CacheState(
    val trimOffsetMillis: Long = 0L,
    val totalDurationMillis: Long = 0L,
    val filename: String = "",
    val selectedSaveOptionIndex: Int = 0,
    val downloadUrl: String = "",
) : Parcelable {
    @IgnoredOnParcel
    val trimRange = TrimRange(
        startPointMillis = trimOffsetMillis,
        totalDurationMillis = totalDurationMillis,
    )

    @IgnoredOnParcel
    val isCacheButtonClickable = filename.isNotBlank()

    @IgnoredOnParcel
    val cacheFormat = Formats.entries[selectedSaveOptionIndex]
}
