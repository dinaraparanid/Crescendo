package com.paranid5.crescendo.domain.utils.extensions

import androidx.media3.common.MediaItem
import com.paranid5.crescendo.domain.tracks.Track

fun List<Track>.toMediaItemList() = map { MediaItem.fromUri(it.path) }