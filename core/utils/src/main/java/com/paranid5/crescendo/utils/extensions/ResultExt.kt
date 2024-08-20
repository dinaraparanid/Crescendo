package com.paranid5.crescendo.utils.extensions

import arrow.core.Either

fun <T> Result<T>.toEither() = fold(
    onSuccess = { Either.Right(it) },
    onFailure = { Either.Left(it) },
)
