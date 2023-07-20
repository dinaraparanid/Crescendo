package com.paranid5.mediastreamer.presentation.search_stream

import com.paranid5.mediastreamer.domain.stream_service.StreamServiceAccessor
import com.paranid5.mediastreamer.presentation.UIHandler

class SearchStreamUIHandler(private val serviceAccessor: StreamServiceAccessor) : UIHandler {
    fun startStreaming(url: String?) = serviceAccessor.startStreaming(url)
}