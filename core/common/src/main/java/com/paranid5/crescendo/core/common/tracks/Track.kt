package com.paranid5.crescendo.core.common.tracks

interface Track {
    val androidId: Long
    val title: String
    val artist: String
    val album: String
    val path: String
    val durationMillis: Long
    val displayName: String
    val dateAdded: Long
    val numberInAlbum: Int
}