package com.paranid5.crescendo.ui.foundation

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
inline fun rememberPullRefreshWithDuration(
    isRefreshing: Boolean,
    minDuration: Long = 500L,
    crossinline onRefresh: () -> Unit,
): Pair<PullRefreshState, Boolean> {
    var isRefreshingAnimMinDone by remember { mutableStateOf(true) }

    val isRefreshingShown by remember(isRefreshing, isRefreshingAnimMinDone) {
        derivedStateOf { isRefreshing || isRefreshingAnimMinDone.not() }
    }

    val refreshScope = rememberCoroutineScope()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshingShown,
        onRefresh = {
            refreshScope.launch {
                isRefreshingAnimMinDone = false
                onRefresh()
                delay(minDuration)
                isRefreshingAnimMinDone = true
            }
        },
    )

    return pullRefreshState to isRefreshingShown
}
