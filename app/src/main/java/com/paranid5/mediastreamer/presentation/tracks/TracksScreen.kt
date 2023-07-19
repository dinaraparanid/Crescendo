package com.paranid5.mediastreamer.presentation.tracks

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.data.tracks.Track
import com.paranid5.mediastreamer.data.tracks.artistAlbum
import com.paranid5.mediastreamer.presentation.Screens
import com.paranid5.mediastreamer.presentation.ui.rememberTrackCoverPainter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@Composable
fun TracksScreen(
    tracksViewModel: TracksViewModel,
    curScreenState: MutableStateFlow<Screens>,
    modifier: Modifier = Modifier
) {
    curScreenState.update { Screens.Tracks }

    val context = LocalContext.current
    val tracks by tracksViewModel.presenter.tracksState.collectAsState()

    LaunchedEffect(Unit) {
        tracksViewModel.presenter.tracksState.update {
            context.allTracksFromMediaStore
        }
    }

    LazyColumn(modifier = modifier.fillMaxSize().padding(10.dp)) {
        items(items = tracks, key = { it.path }) {
            TrackItem(track = it)
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
fun TrackItem(track: Track, modifier: Modifier = Modifier) {
    val trackCover = rememberTrackCoverPainter(
        path = track.path,
        isPlaceholderRequired = true,
        size = 200 to 200
    )

    Row(modifier.fillMaxWidth()) {
        Image(
            painter = trackCover,
            contentDescription = stringResource(id = R.string.track_cover),
            alignment = Alignment.Center,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.CenterVertically)
                .clip(RoundedCornerShape(7.dp))
        )

        Spacer(Modifier.width(5.dp))

        Column(Modifier.fillMaxWidth(1F).padding(start = 10.dp)) {
            Text(
                modifier = Modifier.align(Alignment.Start),
                text = track.title,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                modifier = Modifier.align(Alignment.Start),
                text = track.artistAlbum,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(Modifier.width(5.dp))

        IconButton(
            modifier = Modifier.fillMaxHeight(1F),
            onClick = { /** TODO: Track settings */ }
        ) {
            Icon(
                modifier = Modifier.height(20.dp),
                painter = painterResource(R.drawable.three_dots),
                contentDescription = stringResource(R.string.settings),
                tint = Color.White
            )
        }
    }
}