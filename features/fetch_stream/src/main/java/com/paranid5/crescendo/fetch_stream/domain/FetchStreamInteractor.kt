package com.paranid5.crescendo.fetch_stream.domain

import com.paranid5.crescendo.system.services.stream.StreamServiceAccessor

class FetchStreamInteractor(private val serviceAccessor: StreamServiceAccessor) {
    fun startStreaming(url: String) = serviceAccessor.startStreaming(url)
}