package com.paranid5.crescendo.utils.extensions

private val TIME_REGEX = Regex("(\\d{2}):(\\d{2}):(\\d{2})(\\.(\\d{3}))?")

fun String.toTimeOrNull(): Long? {
    val result = TIME_REGEX.find(this) ?: return null
    return result.groupValues.drop(1).mapNotNull(String::toIntOrNull).totalTimeMs
}

private inline val List<Int>.totalTimeMs
    get() = this
        .zip(arrayOf(3600000L, 60000L, 1000L, 1L))
        .sumOf { (units, unitWeight) -> units * unitWeight }

fun String.safeRepeat(times: Int) = when {
    times <= 0 -> ""
    else -> repeat(times)
}