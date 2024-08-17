package com.paranid5.crescendo.feature.stream.fetch.view_model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class FetchStreamState(
    val url: String = "",
) : Parcelable