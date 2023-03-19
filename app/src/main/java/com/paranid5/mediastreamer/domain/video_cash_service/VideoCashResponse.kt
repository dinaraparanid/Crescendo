package com.paranid5.mediastreamer.domain.video_cash_service

sealed interface VideoCashResponse : java.io.Serializable {
    object Success : VideoCashResponse {
        private const val serialVersionUID = -7333827722639364464L
    }

    data class Error(
        @JvmField val httpCode: Int,
        @JvmField val description: String
    ) : VideoCashResponse {
        companion object {
            private const val serialVersionUID = 2183653321912760986L
        }
    }
}
