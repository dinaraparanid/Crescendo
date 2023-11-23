package com.paranid5.crescendo.data.utils.extensions

fun String.toTime() = split(':').map(String::toInt).sumTime

@Suppress("UNCHECKED_CAST")
fun String.toTimeOrNull() = split(':')
    .takeIf { units -> units.size == 3 && units.all { it.length > 1 } }
    ?.map(String::toIntOrNull)
    ?.takeIf { units -> units.all { it != null } }
    ?.let { it as List<Int> }
    ?.sumTime

private inline val List<Int>.sumTime
    get() = asReversed()
        .zip(arrayOf(1000L, 60000L, 3600000L))
        .sumOf { (units, unitWeight) -> units * unitWeight }