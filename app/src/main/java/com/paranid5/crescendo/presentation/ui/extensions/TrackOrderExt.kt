package com.paranid5.crescendo.presentation.ui.extensions

import android.content.Context
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.core.common.tracks.TrackOrder

fun com.paranid5.crescendo.core.common.tracks.TrackOrder.toString(context: Context) = when (orderType) {
    com.paranid5.crescendo.core.common.tracks.TrackOrder.TrackOrderType.ASC -> when (contentOrder) {
        com.paranid5.crescendo.core.common.tracks.TrackOrder.TrackContentOrder.TITLE -> "^ ${context.getString(R.string.title)}"
        com.paranid5.crescendo.core.common.tracks.TrackOrder.TrackContentOrder.ARTIST -> "^ ${context.getString(R.string.artist)}"
        com.paranid5.crescendo.core.common.tracks.TrackOrder.TrackContentOrder.ALBUM -> "^ ${context.getString(R.string.album)}"
        com.paranid5.crescendo.core.common.tracks.TrackOrder.TrackContentOrder.DATE -> "^ ${context.getString(R.string.date)}"
        com.paranid5.crescendo.core.common.tracks.TrackOrder.TrackContentOrder.NUMBER_IN_ALBUM -> "^ ${context.getString(R.string.number_in_album)}"
    }

    com.paranid5.crescendo.core.common.tracks.TrackOrder.TrackOrderType.DESC -> when (contentOrder) {
        com.paranid5.crescendo.core.common.tracks.TrackOrder.TrackContentOrder.TITLE -> "v ${context.getString(R.string.title)}"
        com.paranid5.crescendo.core.common.tracks.TrackOrder.TrackContentOrder.ARTIST -> "v ${context.getString(R.string.artist)}"
        com.paranid5.crescendo.core.common.tracks.TrackOrder.TrackContentOrder.ALBUM -> "v ${context.getString(R.string.album)}"
        com.paranid5.crescendo.core.common.tracks.TrackOrder.TrackContentOrder.DATE -> "v ${context.getString(R.string.date)}"
        com.paranid5.crescendo.core.common.tracks.TrackOrder.TrackContentOrder.NUMBER_IN_ALBUM -> "v ${context.getString(R.string.number_in_album)}"
    }
}