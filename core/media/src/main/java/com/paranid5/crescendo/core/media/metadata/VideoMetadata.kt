package com.paranid5.crescendo.core.media.metadata

import com.paranid5.crescendo.core.common.metadata.VideoMetadata
import com.paranid5.yt_url_extractor_kt.VideoMeta

object VideoMetadata {
    fun fromYtMeta(ytMeta: VideoMeta) =
        VideoMetadata(
            title = ytMeta.title,
            author = ytMeta.author,
            durationMillis = ytMeta.videoLengthSecs * 1000,
            isLiveStream = ytMeta.isLiveStream,
            covers = ytMeta.run {
                listOf(maxResImageUrl, hqImageUrl, mqImageUrl, sdImageUrl, thumbnailUrl)
            }
        )
}