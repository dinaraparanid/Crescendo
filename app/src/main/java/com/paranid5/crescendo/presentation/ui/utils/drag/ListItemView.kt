package com.paranid5.crescendo.presentation.ui.utils.drag

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.collections.immutable.ImmutableList

typealias ListItemView<T> = @Composable (
    items: ImmutableList<T>,
    index: Int,
    modifier: Modifier
) -> Unit