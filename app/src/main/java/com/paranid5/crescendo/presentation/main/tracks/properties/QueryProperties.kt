package com.paranid5.crescendo.presentation.main.tracks.properties

import com.paranid5.crescendo.presentation.main.tracks.TracksViewModel

inline val TracksViewModel.queryState
    get() = queryStateHolder.queryState

fun TracksViewModel.setQuery(query: String?) =
    queryStateHolder.setQuery(query)