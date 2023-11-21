package com.paranid5.crescendo.presentation.playing

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.R
import com.paranid5.crescendo.domain.StorageHandler
import com.paranid5.crescendo.presentation.tracks.TrackPropertiesButton
import com.paranid5.crescendo.presentation.ui.AudioStatus
import com.paranid5.crescendo.presentation.ui.extensions.getLightMutedOrPrimary
import org.koin.compose.koinInject

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TitleAndAuthor(
    title: String,
    author: String,
    palette: Palette?,
    modifier: Modifier = Modifier,
    textAlignment: Alignment.Horizontal = Alignment.Start,
) {
    val paletteColor = palette.getLightMutedOrPrimary()

    Column(modifier) {
        Text(
            text = title,
            fontSize = 20.sp,
            maxLines = 1,
            color = paletteColor,
            modifier = Modifier
                .basicMarquee(iterations = Int.MAX_VALUE)
                .align(textAlignment)
        )

        Spacer(Modifier.height(5.dp))

        Text(
            text = author,
            fontSize = 18.sp,
            maxLines = 1,
            color = paletteColor,
            modifier = Modifier
                .basicMarquee(iterations = Int.MAX_VALUE)
                .align(textAlignment)
        )
    }
}

@Composable
fun PropertiesButton(
    palette: Palette?,
    audioStatus: AudioStatus?,
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = koinInject()
) {
    val currentTrackMb by storageHandler.currentTrackState.collectAsState()

    when (audioStatus) {
        AudioStatus.STREAMING -> VideoPropertiesButton(
            tint = palette.getLightMutedOrPrimary(),
            modifier = modifier
        )

        else -> currentTrackMb?.let { currentTrack ->
            TrackPropertiesButton(
                track = currentTrack,
                tint = palette.getLightMutedOrPrimary(),
                modifier = modifier,
                iconModifier = Modifier.height(50.dp).width(25.dp),
            )
        }
    }
}

@Composable
private fun VideoPropertiesButton(tint: Color, modifier: Modifier = Modifier) =
    IconButton(
        modifier = modifier,
        onClick = { /** TODO: Video properties */ }
    ) {
        Icon(
            painter = painterResource(R.drawable.three_dots),
            contentDescription = stringResource(R.string.settings),
            tint = tint,
            modifier = Modifier
                .height(50.dp)
                .width(25.dp),
        )
    }

@Composable
fun TitleAndPropertiesButton(
    title: String,
    author: String,
    palette: Palette?,
    audioStatus: AudioStatus?,
    modifier: Modifier = Modifier,
    textAlignment: Alignment.Horizontal = Alignment.Start
) = Row(modifier) {
    TitleAndAuthor(
        title = title,
        author = author,
        modifier = Modifier.weight(1F),
        palette = palette,
        textAlignment = textAlignment
    )

    Spacer(Modifier.width(10.dp))
    PropertiesButton(palette, audioStatus)
}