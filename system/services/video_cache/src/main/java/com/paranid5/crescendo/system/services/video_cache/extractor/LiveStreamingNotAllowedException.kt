package com.paranid5.crescendo.system.services.video_cache.extractor

internal class LiveStreamingNotAllowedException : Exception("Live streaming is not allowed") {
    companion object {
        private const val serialVersionUID = -130135944349375023L
    }
}