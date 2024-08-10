package com.paranid5.crescendo.feature.play.main.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.feature.play.main.presentation.ui.pager.PrimaryPager
import com.paranid5.crescendo.feature.play.main.view_model.PlayState
import com.paranid5.crescendo.feature.play.main.view_model.PlayUiIntent

@Composable
internal fun PrimaryScreen(
    state: PlayState,
    onUiIntent: (PlayUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) = Column(modifier) {
    PlaySearchBar(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.padding.extraMedium),
    )

    Spacer(Modifier.height(dimensions.padding.extraBig))

    PlayScreenCards(Modifier.fillMaxWidth())

    Spacer(Modifier.height(dimensions.padding.big))

    PrimaryPager(
        state = state,
        onUiIntent = onUiIntent,
        modifier = Modifier.fillMaxWidth(),
    )
}
