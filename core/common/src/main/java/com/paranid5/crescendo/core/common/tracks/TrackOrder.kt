package com.paranid5.crescendo.core.common.tracks

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

data class TrackOrder(val contentOrder: TrackContentOrder, val orderType: TrackOrderType) {
    enum class TrackContentOrder {
        TITLE, ARTIST, ALBUM, DATE, NUMBER_IN_ALBUM
    }

    enum class TrackOrderType { ASC, DESC }

    companion object {
        val default
            get() = TrackOrder(TrackContentOrder.DATE, TrackOrderType.DESC)
    }
}

infix fun <T : Track> Iterable<T>.sortedBy(trackOrder: TrackOrder) = when (trackOrder.orderType) {
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

infix fun <T : Track> ImmutableList<T>.sortedBy(trackOrder: TrackOrder) =
    (this as Iterable<T>)
        .sortedBy(trackOrder)
        .toImmutableList()