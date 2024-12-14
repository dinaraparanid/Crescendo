package com.paranid5.crescendo.trimmer.presentation.ui

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography
import com.paranid5.crescendo.trimmer.view_model.TrimmerState
import com.paranid5.crescendo.ui.foundation.AppText
import com.paranid5.crescendo.ui.foundation.getOrNull

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
        AppText(
            text = track?.title.orEmpty(),
            modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE),
            style = typography.h.h2.copy(
                color = colors.text.primary,
                fontWeight = FontWeight.W700,
            ),
        )

        AppText(
            text = track?.artist.orEmpty(),
            modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE),
            style = typography.body.copy(
                color = colors.text.primary,
            ),
        )
    }
}
