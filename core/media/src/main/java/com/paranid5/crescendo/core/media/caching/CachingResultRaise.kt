package com.paranid5.crescendo.core.media.caching

import arrow.core.raise.Raise
import arrow.core.raise.recover
import com.paranid5.crescendo.core.media.files.MediaFile
import kotlin.experimental.ExperimentalTypeInference

@JvmInline
value class CachingResultRaise(private val raise: Raise<CachingResult>) :
    Raise<CachingResult> by raise {
    fun CachingResult.bind() =
        when (this) {
            is CachingResult.Success -> file
            is CachingResult.DownloadResult.Success -> file
            else -> raise.raise(this)
        }
}

@OptIn(ExperimentalTypeInference::class)
inline fun cachingResult(
    @BuilderInference block: CachingResultRaise.() -> MediaFile
): CachingResult = recover(
    block = { CachingResult.Success(block(CachingResultRaise(this))) },
    recover = { it }
)
