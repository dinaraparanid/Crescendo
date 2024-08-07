package com.paranid5.crescendo.ui.drag

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.collections.immutable.ImmutableList

typealias ListItemContent<T> = @Composable (
    items: ImmutableList<T>,
    index: Int,
    modifier: Modifier
) -> Unit