package com.paranid5.crescendo.trimmer.presentation.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors
import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel
import com.paranid5.crescendo.trimmer.presentation.properties.compose.collectTrackAsState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun TitleArtistColumn(
    modifier: Modifier = Modifier,
    viewModel: TrimmerViewModel = koinViewModel(),
    spaceBetween: Dp = 8.dp
) {
    val colors = LocalAppColors.current
    val track by viewModel.collectTrackAsState()

    Column(modifier) {
        Text(
            text = track?.title ?: "",
            fontSize = 20.sp,
            color = colors.fontColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .basicMarquee(iterations = Int.MAX_VALUE)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(spaceBetween))

        Text(
            text = track?.artist ?: "",
            fontSize = 16.sp,
            color = colors.fontColor,
            modifier = Modifier
                .basicMarquee(iterations = Int.MAX_VALUE)
                .align(Alignment.CenterHorizontally)
        )
    }
}