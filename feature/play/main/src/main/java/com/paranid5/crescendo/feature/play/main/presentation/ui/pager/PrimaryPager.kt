package com.paranid5.crescendo.feature.play.main.presentation.ui.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.feature.play.main.view_model.PlayState
import com.paranid5.crescendo.feature.play.main.view_model.PlayState.PagerState
import com.paranid5.crescendo.feature.play.main.view_model.PlayUiIntent
import com.paranid5.crescendo.tracks.presentation.TracksScreen
import com.paranid5.crescendo.tracks.view_model.TracksScreenEffect
import com.paranid5.crescendo.ui.pager.AppPager
import com.paranid5.crescendo.ui.pager.PagerUiState
import com.paranid5.crescendo.utils.doNothing
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun PrimaryPager(
    state: PlayState,
    onUiIntent: (PlayUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activePageIndex by rememberActivePageIndex(state = state)

    AppPager(
        pagerUiStates = rememberPrimaryPagerUiStates(),
        activePage = activePageIndex,
        modifier = modifier,
        onActivePageChanged = { pageIndex ->
            onUiIntent(PlayUiIntent.UpdatePagerState(PagerState.entries[pageIndex]))
        },
    ) { page ->
        val pageModifier = Modifier.fillMaxSize()

        when (PagerState.entries.getOrNull(page)) {
            PagerState.TRACKS -> TracksScreen(
                modifier = pageModifier,
                searchQuery = state.searchQuery,
            ) { result ->
                when (result) {
                    is TracksScreenEffect.ShowTrimmer ->
                        onUiIntent(PlayUiIntent.ShowTrimmer(result.trackUri))

                    TracksScreenEffect.ShowMetaEditor -> doNothing // TODO: show meta editor
                }
            }

            PagerState.ARTISTS -> Box(pageModifier) {
                Text("TODO: Artists", Modifier.align(Alignment.Center))
            }

            PagerState.ALBUMS -> Box(pageModifier) {
                Text("TODO: Albums", Modifier.align(Alignment.Center))
            }

            null -> doNothing
        }
    }
}

@Composable
private fun rememberPrimaryPagerUiStates(): ImmutableList<PagerUiState> {
    val tracks = stringResource(R.string.tracks)
    val artists = stringResource(R.string.artists)
    val albums = stringResource(R.string.albums)
    return remember(tracks, artists, albums) {
        persistentListOf(
            PagerUiState(title = tracks),
            PagerUiState(title = artists),
            PagerUiState(title = albums),
        )
    }
}

@Composable
private fun rememberActivePageIndex(state: PlayState) =
    remember(state) { derivedStateOf { state.pagerState.ordinal } }
