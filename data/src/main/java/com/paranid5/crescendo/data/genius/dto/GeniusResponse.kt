package com.paranid5.crescendo.data.genius.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
internal value class GeniusResponse<T>(@SerialName("response") val response: T)
