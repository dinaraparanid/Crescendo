package com.paranid5.crescendo.core.common.api

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class HttpStatusCode(val value: Int) {
    companion object {
        const val FORBIDDEN_403 = 403
    }
}

inline val HttpStatusCode.isSuccess
    get() = value in 200 until 300

inline val HttpStatusCode.isError
    get() = value >= 400

inline val HttpStatusCode.isClientError
    get() = value in 400 until 500

inline val HttpStatusCode.isServerError
    get() = value >= 500

inline val HttpStatusCode.isForbidden
    get() = value == HttpStatusCode.FORBIDDEN_403