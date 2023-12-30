package com.paranid5.crescendo.presentation.main.trimmer.views

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
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.properties.compose.collectTrackAsState
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TitleArtistColumn(
    modifier: Modifier = Modifier,
    viewModel: TrimmerViewModel = koinActivityViewModel(),
    spaceBetween: Dp = 8.dp
) {
    val colors = LocalAppColors.current
    val track by viewModel.collectTrackAsState()

    Column(modifier) {
        Text(
            text = track!!.title,
            fontSize = 20.sp,
            color = colors.fontColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .basicMarquee(iterations = Int.MAX_VALUE)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(spaceBetween))

        Text(
            text = track!!.artist,
            fontSize = 16.sp,
            color = colors.fontColor,
            modifier = Modifier
                .basicMarquee(iterations = Int.MAX_VALUE)
                .align(Alignment.CenterHorizontally)
        )
    }
}