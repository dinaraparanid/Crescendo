package com.paranid5.crescendo.data.media_store.tracks

import android.content.Context
import android.provider.MediaStore
import arrow.core.curried

fun getTrackFromMediaStore(context: Context, trackPath: String) =
    trackMediaStoreQuery(context, trackPath)
        ?.use(::getNextTrackOrNull.curried()(context))

private fun trackMediaStoreQuery(context: Context, trackPath: String) =
    try {
        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            commonProjection,
            selection,
            arrayOf(trackPath),
            commonOrder
        )
    } catch (e: SecurityException) {
        // permission not granted
        null
    }

private inline val selection
    get() = "${MediaStore.Audio.Media.DATA} = ? AND $commonSelection"
