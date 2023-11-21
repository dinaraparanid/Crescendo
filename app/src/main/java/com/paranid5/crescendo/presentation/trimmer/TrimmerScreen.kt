package com.paranid5.crescendo.presentation.trimmer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paranid5.crescendo.data.utils.extensions.secsToMillis
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrimmerScreen(trimmerViewModel: TrimmerViewModel, modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current.value
    val track by trimmerViewModel.trackState.collectAsState()
    val waveformScrollState = rememberScrollState()

    Column(modifier) {
        Column(
            Modifier
                .padding(top = 24.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = track!!.title,
                fontSize = 20.sp,
                color = colors.inverseSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .basicMarquee(iterations = Int.MAX_VALUE)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = track!!.artist,
                fontSize = 16.sp,
                color = colors.inverseSurface,
                modifier = Modifier
                    .basicMarquee(iterations = Int.MAX_VALUE)
                    .align(Alignment.CenterHorizontally)
            )
        }

        Spacer(Modifier.height(20.dp))

        Box(
            Modifier
                .height(128.dp)
                .align(Alignment.CenterHorizontally)
                .horizontalScroll(waveformScrollState)
        ) {
            TrimWaveform(
                model = track!!.path,
                durationInMillis = track!!.duration,
                viewModel = trimmerViewModel,
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.Center)
            )
        }
    }
}