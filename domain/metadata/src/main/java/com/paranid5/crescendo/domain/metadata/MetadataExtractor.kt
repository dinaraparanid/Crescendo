package com.paranid5.crescendo.domain.metadata

import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.domain.metadata.model.AudioMetadata
import com.paranid5.crescendo.domain.metadata.model.VideoMetadata
import com.paranid5.yt_url_extractor_kt.VideoMeta

interface MetadataExtractor {
    fun extractAudioMetadata(track: Track): AudioMetadata
    fun extractVideoMetadata(ytMeta: VideoMeta): VideoMetadata
}
