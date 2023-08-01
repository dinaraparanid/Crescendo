package com.paranid5.mediastreamer.data.tracks

import android.content.Context
import androidx.compose.runtime.Immutable
import com.paranid5.mediastreamer.R

@Immutable
data class TrackOrder(val contentOrder: TrackContentOrder, val orderType: TrackOrderType) {
    @Immutable
    enum class TrackContentOrder {
        TITLE, ARTIST, ALBUM, DATE, NUMBER_IN_ALBUM
    }

    @Immutable
    enum class TrackOrderType { ASC, DESC }

    companion object {
        inline val default
            get() = TrackOrder(TrackContentOrder.DATE, TrackOrderType.DESC)
    }
}

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

fun Iterable<Track>.sortedBy(trackOrder: TrackOrder) = when (trackOrder.orderType) {
    TrackOrder.TrackOrderType.ASC -> when (trackOrder.contentOrder) {
        TrackOrder.TrackContentOrder.TITLE -> sortedBy(Track::title)
        TrackOrder.TrackContentOrder.ARTIST -> sortedBy(Track::artist)
        TrackOrder.TrackContentOrder.ALBUM -> sortedBy(Track::album)
        TrackOrder.TrackContentOrder.DATE -> sortedBy(Track::dateAdded)
        TrackOrder.TrackContentOrder.NUMBER_IN_ALBUM -> sortedBy(Track::numberInAlbum)
    }

    TrackOrder.TrackOrderType.DESC -> when (trackOrder.contentOrder) {
        TrackOrder.TrackContentOrder.TITLE -> sortedByDescending(Track::title)
        TrackOrder.TrackContentOrder.ARTIST -> sortedByDescending(Track::artist)
        TrackOrder.TrackContentOrder.ALBUM -> sortedByDescending(Track::album)
        TrackOrder.TrackContentOrder.DATE -> sortedByDescending(Track::dateAdded)
        TrackOrder.TrackContentOrder.NUMBER_IN_ALBUM -> sortedByDescending(Track::numberInAlbum)
    }
}