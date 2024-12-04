package com.paranid5.crescendo.data.genius.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GeniusResponse<T>(@SerialName("response") val response: T)
