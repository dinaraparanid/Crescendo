package com.paranid5.mediastreamer.presentation.fetch_stream

import com.paranid5.mediastreamer.domain.stream_service.StreamServiceAccessor
import com.paranid5.mediastreamer.presentation.UIHandler

class FetchStreamUIHandler(private val serviceAccessor: StreamServiceAccessor) : UIHandler {
    fun startStreaming(url: String?) = serviceAccessor.startStreaming(url)
}