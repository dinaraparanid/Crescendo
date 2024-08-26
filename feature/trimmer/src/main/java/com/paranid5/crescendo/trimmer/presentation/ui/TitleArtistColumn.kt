package com.paranid5.crescendo.trimmer.presentation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.trimmer.view_model.TrimmerState
import com.paranid5.crescendo.ui.foundation.getOrNull

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun TitleArtistColumn(
    state: TrimmerState,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(dimensions.padding.small),
) {
    val track = state.trackState.getOrNull()

    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = track?.title.orEmpty(),
            style = typography.h.h2,
            color = colors.text.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE),
        )

        Text(
            text = track?.artist.orEmpty(),
            style = typography.body,
            color = colors.text.primary,
            modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE),
        )
    }
}
