package com.paranid5.mediastreamer.data

import at.huber.youtubeExtractor.VideoMeta
import com.paranid5.mediastreamer.STREAM_WITH_NO_NAME
import com.paranid5.mediastreamer.UNKNOWN_STREAMER
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.qualifier.named

@Serializable
data class VideoMetadata(
    @JvmField val title: String = get(named(STREAM_WITH_NO_NAME)),
    @JvmField val author: String = get(named(UNKNOWN_STREAMER)),
    @JvmField val covers: List<String> = listOf(),
    @JvmField val lenInMillis: Long = 0
) {
    private companion object : KoinComponent

    constructor(youtubeMeta: VideoMeta) : this(
        title = youtubeMeta.title,
        author = youtubeMeta.author,
        lenInMillis = youtubeMeta.videoLength * 1000,
        covers = youtubeMeta.run {
            listOf(maxResImageUrl, hqImageUrl, mqImageUrl, sdImageUrl, thumbUrl)
        }
    )
}