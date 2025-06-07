package com.paranid5.crescendo.utils.extensions

import arrow.core.Either

inline fun <T> Either.Companion.catchNonCancellation(block: () -> T) =
    runCatchingNonCancellation(block = block).toEither()
