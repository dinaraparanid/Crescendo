package com.paranid5.crescendo.presentation.ui.extensions

import android.content.Context
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.domain.tracks.TrackOrder

fun TrackOrder.toString(context: Context) = when (orderType) {
    TrackOrder.TrackOrderType.ASC -> when (contentOrder) {
        TrackOrder.TrackContentOrder.TITLE -> "^ ${context.getString(R.string.title)}"
        TrackOrder.TrackContentOrder.ARTIST -> "^ ${context.getString(R.string.artist)}"
        TrackOrder.TrackContentOrder.ALBUM -> "^ ${context.getString(R.string.album)}"
        TrackOrder.TrackContentOrder.DATE -> "^ ${context.getString(R.string.date)}"
        TrackOrder.TrackContentOrder.NUMBER_IN_ALBUM -> "^ ${context.getString(R.string.number_in_album)}"
    }

    TrackOrder.TrackOrderType.DESC -> when (contentOrder) {
        TrackOrder.TrackContentOrder.TITLE -> "v ${context.getString(R.string.title)}"
        TrackOrder.TrackContentOrder.ARTIST -> "v ${context.getString(R.string.artist)}"
        TrackOrder.TrackContentOrder.ALBUM -> "v ${context.getString(R.string.album)}"
        TrackOrder.TrackContentOrder.DATE -> "v ${context.getString(R.string.date)}"
        TrackOrder.TrackContentOrder.NUMBER_IN_ALBUM -> "v ${context.getString(R.string.number_in_album)}"
    }
}