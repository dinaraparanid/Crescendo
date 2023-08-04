package com.paranid5.crescendo.presentation.fetch_stream

import com.paranid5.crescendo.domain.services.stream_service.StreamServiceAccessor
import com.paranid5.crescendo.presentation.UIHandler

class FetchStreamUIHandler(private val serviceAccessor: StreamServiceAccessor) : UIHandler {
    fun startStreaming(url: String) = serviceAccessor.startStreaming(url)
}