package com.paranid5.crescendo.domain.utils.extensions

val Long.timeString: String
    get() {
        var self = this

        val hours = self / 3600000
        self -= hours * 3600000

        val minutes = self / 60000
        self -= minutes * 60000

        val seconds = self / 1000

        return "${hours.toTimeValue(minLength = 2)}:" +
                "${minutes.toTimeValue(minLength = 2)}:" +
                seconds.toTimeValue(minLength = 2)
    }

val Long.timeStringMs: String
    get() {
        var self = this

        val hours = self / 3600000
        self -= hours * 3600000

        val minutes = self / 60000
        self -= minutes * 60000

        val seconds = self / 1000
        self -= seconds * 1000

        val millis = self

        return "${hours.toTimeValue(minLength = 2)}:" +
                "${minutes.toTimeValue(minLength = 2)}:" +
                "${seconds.toTimeValue(minLength = 2)}." +
                millis.toTimeValue(minLength = 3)
    }

private fun Long.toTimeValue(minLength: Int) =
    toString().fillWithZeroes(minLength)

private fun String.fillWithZeroes(minLength: Int) =
    "${"0".safeRepeat(minLength - length)}$this"