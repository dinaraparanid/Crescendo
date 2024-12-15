package com.paranid5.crescendo.data.metadata

import arrow.core.andThen
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.domain.image.model.Image
import com.paranid5.crescendo.domain.image.model.ImagePath
import com.paranid5.crescendo.domain.image.model.ImageUrl
import com.paranid5.crescendo.domain.metadata.MetadataExtractor
import com.paranid5.crescendo.domain.metadata.model.AudioMetadata
import com.paranid5.crescendo.domain.metadata.model.VideoMetadata
import com.paranid5.yt_url_extractor_kt.VideoMeta

class MetadataExtractorImpl : MetadataExtractor {
    override fun extractAudioMetadata(track: Track): AudioMetadata =
        AudioMetadata(
            title = track.title,
            author = track.artist,
            album = track.album,
            covers = listOf(Image.Path(ImagePath(track.path))),
            durationMillis = track.durationMillis
        )

    override fun extractVideoMetadata(ytMeta: VideoMeta): VideoMetadata =
        VideoMetadata(
            title = ytMeta.title,
            author = ytMeta.author,
            durationMillis = ytMeta.videoLengthSecs * 1000,
            isLiveStream = ytMeta.isLiveStream,
            covers = ytMeta.run {
                listOf(maxResImageUrl, hqImageUrl, mqImageUrl, sdImageUrl, thumbnailUrl)
                    .map(::ImageUrl andThen Image::Url)
            }
        )
}