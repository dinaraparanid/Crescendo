package com.paranid5.mediastreamer.utils.extensions

fun Float.toPlaybackPosition(millisInPercentage: Float) = (this * millisInPercentage).toLong()