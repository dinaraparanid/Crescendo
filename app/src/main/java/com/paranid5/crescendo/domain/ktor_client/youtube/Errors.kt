package com.paranid5.crescendo.domain.ktor_client.youtube

open class YtException(message: String? = null) : RuntimeException(message)

class WrongYtUrlFormatException : YtException("Wrong YouTube URL format")

class YtPlayerResponseNotFoundException : YtException("ytPlayerResponse was not found")

class YtPlayerResponseStructureChangedException : YtException("ytPlayerResponse structure changed")

class YtFilesNotFoundException : YtException("Yt files not found")

fun <T> YtFailure(exception: YtException) = Result.failure<T>(exception)