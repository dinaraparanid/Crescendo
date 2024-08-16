package com.paranid5.crescendo.ui.drag

import androidx.compose.foundation.lazy.LazyListItemInfo

internal inline val LazyListItemInfo.offsetEnd: Int
    get() = this.offset + this.size
