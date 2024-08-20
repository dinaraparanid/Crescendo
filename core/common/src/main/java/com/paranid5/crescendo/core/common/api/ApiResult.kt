package com.paranid5.crescendo.core.common.api

import arrow.core.Either

typealias ApiResult<L, R> = Either<Throwable, Either<L, R>>
typealias ApiResultWithCode<T> = ApiResult<HttpStatusCode, T>