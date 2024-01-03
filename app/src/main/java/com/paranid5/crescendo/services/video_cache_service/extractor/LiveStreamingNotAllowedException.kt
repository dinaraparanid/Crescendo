package com.paranid5.crescendo.services.video_cache_service.extractor

class LiveStreamingNotAllowedException : Exception("Live streaming is not allowed") {
    companion object {
        private const val serialVersionUID = -130135944349375023L
    }
}