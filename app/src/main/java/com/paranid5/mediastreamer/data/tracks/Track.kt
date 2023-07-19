package com.paranid5.mediastreamer.data.tracks

import android.os.Parcelable

interface Track : Parcelable {
    val androidId: Long
    val title: String
    val artist: String
    val album: String
    val path: String
    val duration: Long
    val displayName: String
    val dateAdded: Long
    val numberInAlbum: Int
}

inline val Track.artistAlbum
    get() = "$artist/$album"