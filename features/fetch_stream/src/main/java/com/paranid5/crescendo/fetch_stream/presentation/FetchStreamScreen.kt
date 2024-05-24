package com.paranid5.crescendo.fetch_stream.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.fetch_stream.presentation.views.DownloadButton
import com.paranid5.crescendo.fetch_stream.presentation.views.PlayButton
import com.paranid5.crescendo.fetch_stream.presentation.views.UrlEditor

@Composable
fun FetchStreamScreen(modifier: Modifier = Modifier) =
    Box(modifier.orientedPadding()) {
        Column(
            Modifier
                .align(Alignment.Center)
                .width(300.dp)
        ) {
            UrlEditor(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(12.dp))

            PlayButton(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(8.dp))

            DownloadButton(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )
        }
    }

@Composable
private fun Modifier.orientedPadding() =
    when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> this.padding(bottom = 16.dp)
        else -> this
    }