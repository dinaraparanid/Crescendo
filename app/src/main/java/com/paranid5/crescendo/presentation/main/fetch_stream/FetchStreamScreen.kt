package com.paranid5.crescendo.presentation.main.fetch_stream

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.paranid5.crescendo.presentation.main.fetch_stream.views.ConfirmButton
import com.paranid5.crescendo.presentation.main.fetch_stream.views.UrlEditor

@Composable
fun FetchStreamScreen(
    viewModel: FetchStreamViewModel,
    modifier: Modifier = Modifier,
) {
    Box(modifier.orientedPadding()) {
        Column(Modifier.align(Alignment.Center)) {
            UrlEditor(
                viewModel = viewModel,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 10.dp)
                    .width(300.dp)
            )

            ConfirmButton(
                viewModel = viewModel,
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun Modifier.orientedPadding(): Modifier {
    val config = LocalConfiguration.current

    return when (config.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> this.padding(bottom = 15.dp)
        else -> this
    }
}