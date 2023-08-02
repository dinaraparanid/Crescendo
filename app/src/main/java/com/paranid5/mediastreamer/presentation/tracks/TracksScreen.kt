package com.paranid5.mediastreamer.presentation.tracks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.data.tracks.Track
import com.paranid5.mediastreamer.data.tracks.TrackOrder
import com.paranid5.mediastreamer.data.tracks.artistAlbum
import com.paranid5.mediastreamer.data.tracks.sortedBy
import com.paranid5.mediastreamer.data.tracks.toDefaultTrackList
import com.paranid5.mediastreamer.data.tracks.toString
import com.paranid5.mediastreamer.domain.StorageHandler
import com.paranid5.mediastreamer.domain.services.track_service.TrackServiceAccessor
import com.paranid5.mediastreamer.presentation.Screens
import com.paranid5.mediastreamer.presentation.ui.AudioStatus
import com.paranid5.mediastreamer.presentation.ui.permissions.requests.audioRecordingPermissionsRequestLauncher
import com.paranid5.mediastreamer.presentation.ui.permissions.requests.foregroundServicePermissionsRequestLauncher
import com.paranid5.mediastreamer.presentation.ui.rememberTrackCoverModel
import com.paranid5.mediastreamer.presentation.ui.theme.LocalAppColors
import com.paranid5.mediastreamer.presentation.ui.utils.Searcher
import com.paranid5.mediastreamer.presentation.ui.utils.Spinner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun TracksScreen(
    tracksViewModel: TracksViewModel,
    curScreenState: MutableStateFlow<Screens>,
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = koinInject()
) {
    val context = LocalContext.current
    curScreenState.update { Screens.Tracks }

    val allTracks by tracksViewModel.presenter.tracksState.collectAsState()
    val filterTracksState = remember { mutableStateOf(listOf<Track>()) }
    var showsTracks by remember { mutableStateOf(listOf<Track>()) }

    var tracksScrollingState = rememberLazyListState()
    val isSearchBarActiveState = remember { mutableStateOf(false) }
    val trackOrder by storageHandler.trackOrderState.collectAsState()

    val isAudioRecordingPermissionDialogShownState = remember { mutableStateOf(true) }
    val isForegroundServicePermissionDialogShownState = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        tracksViewModel.presenter.tracksState.update {
            context.allTracksFromMediaStore
        }
    }

    LaunchedEffect(key1 = allTracks, key2 = filterTracksState.value, key3 = trackOrder) {
        showsTracks = when (isSearchBarActiveState.value) {
            false -> allTracks.sortedBy(trackOrder)
            true -> filterTracksState.value.sortedBy(trackOrder)
        }
    }

    Box(modifier) {
        Column(Modifier.fillMaxSize()) {
            TrackSearcher(
                tracksState = tracksViewModel.presenter.tracksState,
                queryState = tracksViewModel.presenter.queryState,
                isSearchBarActiveState = isSearchBarActiveState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        when (isSearchBarActiveState.value) {
                            true -> 80.dp
                            false -> 60.dp
                        }
                    )
            ) { filteredTracks, scrollingState ->
                filterTracksState.value = filteredTracks
                tracksScrollingState = scrollingState
            }

            TrackBar(
                tracksNumber = showsTracks.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp)
            )

            Spacer(Modifier.height(10.dp))

            TrackList(
                tracks = showsTracks,
                scrollingState = tracksScrollingState,
                modifier = Modifier
                    .fillMaxSize(1F)
                    .padding(horizontal = 5.dp)
            )
        }
    }
}

// -------------------------------- Track Searcher --------------------------------

@Composable
private fun TrackSearcher(
    tracksState: StateFlow<List<Track>>,
    queryState: MutableStateFlow<String?>,
    isSearchBarActiveState: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    content: @Composable (ColumnScope.(List<Track>, LazyListState) -> Unit)
) = Searcher(
    modifier = modifier,
    allItemsState = tracksState,
    queryState = queryState,
    filteredContent = content,
    isSearchBarActiveState = isSearchBarActiveState,
    filter = { query, track ->
        val title = track.title.lowercase()
        val artist = track.artist.lowercase()
        val album = track.album.lowercase()
        query in title || query in artist || query in album
    }
)

// -------------------------------- Track Bar --------------------------------

@Composable
private fun TrackBar(
    tracksNumber: Int,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAppColors.current.value

    Row(modifier) {
        Text(
            text = "${stringResource(R.string.tracks)}: $tracksNumber",
            color = colors.primary,
            textAlign = TextAlign.Start,
            modifier = Modifier.align(Alignment.CenterVertically)
        )

        TrackOrderSpinner(
            Modifier
                .align(Alignment.CenterVertically)
                .fillMaxWidth()
                .height(40.dp)
        )
    }
}

@Composable
private fun TrackOrderSpinner(
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = koinInject()
) {
    val context = LocalContext.current
    val colors = LocalAppColors.current.value
    val scope = rememberCoroutineScope()
    val trackOrder by storageHandler.trackOrderState.collectAsState()

    val spinnerItems = listOf(
        stringResource(R.string.title),
        stringResource(R.string.artist),
        stringResource(R.string.album),
        stringResource(R.string.date),
        stringResource(R.string.number_in_album),
        stringResource(R.string.asc),
        stringResource(R.string.desc)
    ).map { "${stringResource(R.string.by)} $it" }

    val selectedItemIndexes by remember {
        derivedStateOf {
            val firstInd = trackOrder.contentOrder.ordinal
            val contentOrders = TrackOrder.TrackContentOrder.entries
            val secondInd = trackOrder.orderType.ordinal + contentOrders.size
            listOf(firstInd, secondInd)
        }
    }

    Box(modifier) {
        Spinner(
            items = spinnerItems,
            selectedItemIndexes = selectedItemIndexes,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterStart),
            onItemSelected = { ind, _ ->
                scope.launch {
                    storageHandler.storeTrackOrder(selectedTrackOrder(ind, trackOrder))
                }
            },
            previewItemFactory = { _, _, _ -> },
            selectedItemFactory = { _, item, modifier ->
                SelectedOrderItem(item, modifier.fillMaxWidth())
            },
            dropdownItemFactory = { _, item, modifier ->
                OrderItem(item, modifier.fillMaxWidth())
            }
        )

        Text(
            text = trackOrder.toString(context),
            color = colors.primary,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Composable
private fun SelectedOrderItem(item: String, modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current.value
    val primaryColor = colors.primary
    val backgroundColor = colors.background

    Row(modifier) {
        Checkbox(
            checked = true,
            onCheckedChange = null,
            modifier = Modifier.align(Alignment.CenterVertically),
            colors = CheckboxDefaults.colors(
                checkedColor = primaryColor,
                checkmarkColor = backgroundColor,
            ),
        )

        Spacer(Modifier.width(10.dp))

        Text(
            text = item,
            color = primaryColor,
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }
}

@Composable
private fun OrderItem(item: String, modifier: Modifier = Modifier) {
    Row(modifier) {
        Checkbox(
            checked = false,
            onCheckedChange = null,
            colors = CheckboxDefaults.colors(uncheckedColor = Color.White),
        )

        Spacer(Modifier.width(10.dp))

        Text(text = item)
    }
}

private fun selectedTrackOrder(
    selectedIndex: Int,
    currentTrackOrder: TrackOrder,
): TrackOrder {
    val contentOrders = TrackOrder.TrackContentOrder.entries
    val orderTypes = TrackOrder.TrackOrderType.entries
    val contentOrdersNum = contentOrders.size

    return when {
        selectedIndex < contentOrdersNum -> {
            val newContentOrder = contentOrders[selectedIndex]
            TrackOrder(newContentOrder, currentTrackOrder.orderType)
        }

        else -> {
            val newOrderType = orderTypes[selectedIndex - contentOrdersNum]
            TrackOrder(currentTrackOrder.contentOrder, newOrderType)
        }
    }
}

// -------------------------------- Track List --------------------------------

@Composable
private fun TrackList(
    tracks: List<Track>,
    scrollingState: LazyListState,
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = koinInject(),
    trackServiceAccessor: TrackServiceAccessor = koinInject()
) {
    val scope = rememberCoroutineScope()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        state = scrollingState,
        modifier = modifier
    ) {
        itemsIndexed(
            items = tracks,
            key = { _, track -> track.path }
        ) { ind, _ ->
            TrackItem(
                tracks = tracks,
                trackInd = ind,
                scope = scope,
                storageHandler = storageHandler,
                trackServiceAccessor = trackServiceAccessor,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// -------------------------------- Track Item --------------------------------

@Composable
private fun TrackItem(
    tracks: List<Track>,
    trackInd: Int,
    scope: CoroutineScope,
    storageHandler: StorageHandler,
    trackServiceAccessor: TrackServiceAccessor,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current.value
    val currentTrack by storageHandler.currentTrackState.collectAsState()

    val trackMb by remember { derivedStateOf { tracks.getOrNull(trackInd) } }
    val trackPath by remember { derivedStateOf { trackMb?.path } }

    val isTrackCurrent by remember {
        derivedStateOf { trackPath == currentTrack?.path }
    }

    val textColor by remember {
        derivedStateOf { if (isTrackCurrent) colors.primary else Color.White }
    }

    val isForegroundServicePermissionDialogShownState = remember { mutableStateOf(false) }
    val isAudioRecordingPermissionDialogShownState = remember { mutableStateOf(false) }

    trackMb?.let { track ->
        Box(modifier) {
            val (areForegroundPermissionsGranted, launchFSPermissions) = foregroundServicePermissionsRequestLauncher(
                isForegroundServicePermissionDialogShownState,
                modifier = Modifier.align(Alignment.Center)
            )

            val (isRecordingPermissionGranted, launchRecordPermissions) = audioRecordingPermissionsRequestLauncher(
                isAudioRecordingPermissionDialogShownState,
                modifier = Modifier.align(Alignment.Center)
            )

            Row(
                modifier.clickable {
                    if (!areForegroundPermissionsGranted) {
                        launchFSPermissions()
                        return@clickable
                    }

                    if (!isRecordingPermissionGranted) {
                        launchRecordPermissions()
                        return@clickable
                    }

                    scope.launch {
                        onTrackClicked(tracks, trackInd, storageHandler, trackServiceAccessor)
                    }
                }
            ) {
                TrackCover(
                    trackPath = trackPath ?: "",
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.CenterVertically)
                        .clip(RoundedCornerShape(7.dp))
                )

                Spacer(Modifier.width(5.dp))

                TrackInfo(
                    track = track,
                    textColor = textColor,
                    modifier = Modifier
                        .weight(1F)
                        .padding(start = 5.dp)
                        .align(Alignment.CenterVertically)
                )

                Spacer(Modifier.width(5.dp))

                TrackPropertiesButton(iconModifier = Modifier.height(20.dp))
            }
        }
    }
}

private suspend inline fun onTrackClicked(
    tracks: List<Track>,
    trackInd: Int,
    storageHandler: StorageHandler,
    trackServiceAccessor: TrackServiceAccessor
) {
    storageHandler.storeAudioStatus(AudioStatus.PLAYING)

    trackServiceAccessor.startPlaying(
        playlist = tracks.toDefaultTrackList(),
        trackInd = trackInd
    )
}

@Composable
private fun TrackCover(trackPath: String, modifier: Modifier = Modifier) {
    val trackCover = rememberTrackCoverModel(
        path = trackPath,
        isPlaceholderRequired = true,
        size = 200 to 200,
        animationMillis = 250
    )

    AsyncImage(
        model = trackCover,
        contentDescription = stringResource(id = R.string.track_cover),
        alignment = Alignment.Center,
        contentScale = ContentScale.FillBounds,
        modifier = modifier
    )
}

@Composable
private fun TrackInfo(
    track: Track,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        TrackTitle(
            modifier = Modifier.align(Alignment.Start),
            trackTitle = track.title,
            textColor = textColor,
        )

        TrackArtistAlbum(
            modifier = Modifier.align(Alignment.Start),
            trackArtistAlbum = track.artistAlbum,
            textColor = textColor,
        )
    }
}

@Composable
private fun TrackTitle(
    trackTitle: String,
    textColor: Color,
    modifier: Modifier = Modifier
) = TrackText(
    modifier = modifier,
    text = trackTitle,
    textColor = textColor,
    fontSize = 18.sp,
)

@Composable
private fun TrackArtistAlbum(
    trackArtistAlbum: String,
    textColor: Color,
    modifier: Modifier = Modifier
) = TrackText(
    modifier = modifier,
    text = trackArtistAlbum,
    textColor = textColor,
    fontSize = 15.sp,
)

@Composable
private fun TrackText(
    text: String,
    textColor: Color,
    fontSize: TextUnit,
    modifier: Modifier = Modifier
) = Text(
    modifier = modifier,
    text = text,
    color = textColor,
    fontSize = fontSize,
    maxLines = 1,
    overflow = TextOverflow.Ellipsis
)