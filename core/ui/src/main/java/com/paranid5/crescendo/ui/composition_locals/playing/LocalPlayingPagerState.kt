package com.paranid5.crescendo.ui.composition_locals.playing

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.compositionLocalOf

enum class PlayingPage { TRACK, STREAM }

@OptIn(ExperimentalFoundationApi::class)
val LocalPlayingPagerState = compositionLocalOf<PagerState?> { null }