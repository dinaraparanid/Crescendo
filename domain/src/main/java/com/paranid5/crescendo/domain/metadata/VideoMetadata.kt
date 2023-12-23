package com.paranid5.crescendo.domain.metadata

import androidx.compose.runtime.Immutable
import com.paranid5.yt_url_extractor_kt.VideoMeta
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent

@Serializable
@Parcelize
@Immutable
data class VideoMetadata(
    override val title: String? = null,
    override val author: String? = null,
    override val covers: List<String> = listOf(),
    override val durationMillis: Long = 0,
    @JvmField val isLiveStream: Boolean = false
) : Metadata {
    internal companion object : KoinComponent

    constructor(youtubeMeta: VideoMeta) : this(
        title = youtubeMeta.title,
        author = youtubeMeta.author,
        durationMillis = youtubeMeta.videoLengthSecs * 1000,
        isLiveStream = youtubeMeta.isLiveStream,
        covers = youtubeMeta.run {
            listOf(maxResImageUrl, hqImageUrl, mqImageUrl, sdImageUrl, thumbnailUrl)
        }
    )
}