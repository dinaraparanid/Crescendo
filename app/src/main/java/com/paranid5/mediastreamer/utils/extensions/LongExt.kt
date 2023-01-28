package com.paranid5.mediastreamer.utils.extensions

private inline val Long.timeValue
    get() = toString().timeValue

private inline val String.timeValue
    get() = if (length > 1) this else "0$this"

internal inline val Long.timeString: String
    get() {
        var self = this

        val hours = self / 3600000
        self -= hours * 3600000

        val minutes = self / 60000
        self -= minutes * 60000

        val seconds = self / 1000

        return "${hours.timeValue}:${minutes.timeValue}:${seconds.timeValue}"
    }