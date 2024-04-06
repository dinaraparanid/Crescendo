package com.paranid5.crescendo.data.media_store.tracks

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import arrow.core.curried
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

fun allTracksFromMediaStore(context: Context) =
    allTracksMediaStoreQuery(context)
        ?.use(::trackList.curried()(context))
        ?: persistentListOf()

private fun trackList(context: Context, cursor: Cursor) =
    generateSequence { getNextTrackOrNull(context, cursor) }.toImmutableList()

private fun allTracksMediaStoreQuery(context: Context) =
    try {
        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            commonProjection,
            commonSelection,
            null,
            commonOrder
        )
    } catch (e: SecurityException) {
        // permission not granted
        null
    }

