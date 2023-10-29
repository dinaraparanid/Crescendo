package com.paranid5.crescendo.domain.ktor_client.youtube

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DecipherFunctionData(
    @JvmField val decipherJsFileName: String? = null,
    @JvmField val decipherFunctionName: String? = null,
    @JvmField val decipherFunctions: String? = null
) : Parcelable