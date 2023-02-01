package com.paranid5.mediastreamer.presentation.search_stream

import com.paranid5.mediastreamer.presentation.UIHandler
import com.paranid5.mediastreamer.stream_service.StreamServiceAccessor

class SearchStreamUIHandler(private val serviceAccessor: StreamServiceAccessor) : UIHandler {
    fun startStreaming(url: String?) = serviceAccessor.startStreaming(url)
}