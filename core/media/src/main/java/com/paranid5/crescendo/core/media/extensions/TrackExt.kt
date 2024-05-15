package com.paranid5.crescendo.core.media.extensions

import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.core.media.images.getCoverDataByPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

suspend fun Track.getCoverDataAsync() = coroutineScope {
    async(Dispatchers.IO) { getCoverDataByPath(path) }
}