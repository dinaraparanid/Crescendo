package com.paranid5.crescendo.domain.ktor_client.youtube

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class YtFile(val format: Format?, val url: String?) : Parcelable