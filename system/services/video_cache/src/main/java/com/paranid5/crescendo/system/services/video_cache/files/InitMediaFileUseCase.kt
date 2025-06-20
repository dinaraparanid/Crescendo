package com.paranid5.crescendo.system.services.video_cache.files

import com.paranid5.crescendo.caching.entity.CachingResult
import com.paranid5.crescendo.domain.files.MediaFilesRepository
import com.paranid5.crescendo.domain.files.entity.Filename
import com.paranid5.crescendo.domain.files.entity.Formats
import com.paranid5.crescendo.domain.files.entity.fileExtension

internal class InitMediaFileUseCase(
    private val repository: MediaFilesRepository,
) {
    suspend operator fun invoke(
        desiredFilename: String,
        isAudio: Boolean,
    ) = repository
        .createVideoFile(
            mediaDirectory = repository.getInitialVideoDirectory(isAudio),
            filename = Filename(desiredFilename.replace(Regex("\\W+"), "_")),
            ext = Formats.MP4.fileExtension,
        )
        .mapLeft {
            CachingResult.DownloadResult.FileCreationError
        }
}
