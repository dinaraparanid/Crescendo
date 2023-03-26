package com.paranid5.mediastreamer.data.utils.extensions

fun Float.toPlaybackPosition(millisInPercentage: Float) = (this * millisInPercentage).toLong()