package com.paranid5.crescendo.caching.entity

import arrow.core.raise.Raise
import arrow.core.raise.recover
import com.paranid5.crescendo.domain.files.entity.MediaFile
import kotlin.experimental.ExperimentalTypeInference

@JvmInline
value class DownloadResultRaise(
    private val raise: Raise<CachingResult.DownloadResult>
) : Raise<CachingResult.DownloadResult> by raise {

    fun CachingResult.DownloadResult.bind() = when (this) {
        is CachingResult.DownloadResult.Success -> file
        else -> raise.raise(this)
    }
}

@OptIn(ExperimentalTypeInference::class)
inline fun downloadResult(
    @BuilderInference block: DownloadResultRaise.() -> MediaFile,
): CachingResult.DownloadResult = recover(
    block = { CachingResult.DownloadResult.Success(block(DownloadResultRaise(this))) },
    recover = { it }
)
