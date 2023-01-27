package com.paranid5.mediastreamer.utils.extensions

inline val Long.timeString: String
    get() {
        var self = this

        val hours = self / 3600000
        self /= 3600000

        val minutes = self / 60000
        self /= 60000

        val seconds = self / 1000

        return "$hours:$minutes:$seconds"
    }