package com.paranid5.crescendo.presentation.main.fetch_stream

import com.paranid5.crescendo.presentation.UIHandler
import com.paranid5.crescendo.services.stream_service.StreamServiceAccessor

class FetchStreamUIHandler(private val serviceAccessor: StreamServiceAccessor) : UIHandler {
    fun startStreaming(url: String) = serviceAccessor.startStreaming(url)
}