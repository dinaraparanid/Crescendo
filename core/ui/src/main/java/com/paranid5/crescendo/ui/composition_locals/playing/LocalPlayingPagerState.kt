package com.paranid5.crescendo.ui.composition_locals.playing

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.compositionLocalOf

enum class PlayingPage { TRACK, STREAM }

val LocalPlayingPagerState = compositionLocalOf<PagerState?> { null }
