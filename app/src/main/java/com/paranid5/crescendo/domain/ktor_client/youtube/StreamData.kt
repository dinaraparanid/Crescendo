package com.paranid5.crescendo.domain.ktor_client.youtube

import java.util.SortedMap

data class StreamData(
    val ytFiles: SortedMap<Int, YtFile>,
    val livestreamManifests: Result<LiveStreamManifests>,
    val videoMeta: Result<VideoMeta>
)
