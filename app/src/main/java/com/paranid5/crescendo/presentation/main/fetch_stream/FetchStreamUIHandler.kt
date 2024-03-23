package com.paranid5.crescendo.presentation.main.fetch_stream

import com.paranid5.crescendo.presentation.UIHandler
import com.paranid5.crescendo.system.services.stream.StreamServiceAccessor

class FetchStreamUIHandler(private val serviceAccessor: StreamServiceAccessor) : UIHandler {
    fun startStreaming(url: String) = serviceAccessor.startStreaming(url)
}