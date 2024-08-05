package com.paranid5.crescendo.ui.foundation

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AppRefreshIndicator(
    refreshing: Boolean,
    refreshState: PullRefreshState,
    modifier: Modifier = Modifier,
) = PullRefreshIndicator(
    refreshing = refreshing,
    state = refreshState,
    modifier = modifier,
    backgroundColor = colors.background.alternative,
    contentColor = colors.primary,
)
