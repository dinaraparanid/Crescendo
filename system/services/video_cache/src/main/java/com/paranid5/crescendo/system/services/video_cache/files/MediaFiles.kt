package com.paranid5.crescendo.system.services.video_cache.files

import arrow.core.raise.either
import arrow.core.raise.ensure
import com.paranid5.crescendo.core.common.media.MediaFileExtension
import com.paranid5.crescendo.core.media.caching.CachingResult
import com.paranid5.crescendo.core.media.files.createVideoFileCatching
import com.paranid5.crescendo.core.media.files.getInitialVideoDirectory

internal suspend fun initMediaFile(
    desiredFilename: String,
    isAudio: Boolean
) = either {
    val storeFileRes = createVideoFileCatching(
        mediaDirectory = getInitialVideoDirectory(isAudio),
        filename = desiredFilename.replace(Regex("\\W+"), "_"),
        ext = MediaFileExtension("mp4"),
    )

    ensure(storeFileRes.isRight()) {
        CachingResult.DownloadResult.FileCreationError
    }

    storeFileRes.getOrNull()!!
}
