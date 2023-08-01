package com.paranid5.mediastreamer.presentation.playing

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.paranid5.mediastreamer.domain.StorageHandler
import com.paranid5.mediastreamer.presentation.ui.utils.BroadcastReceiver
import com.paranid5.mediastreamer.presentation.ui.extensions.getLightVibrantOrPrimary
import org.koin.compose.koinInject

@Composable
internal fun PlaybackSlider(
    length: Long,
    palette: Palette?,
    modifier: Modifier = Modifier,
    playingUIHandler: PlayingUIHandler = koinInject(),
    storageHandler: StorageHandler = koinInject(),
    content: @Composable RowScope.(curPosition: Long, videoLength: Long, color: Color) -> Unit
) {
    val lightVibrantColor = palette.getLightVibrantOrPrimary()
    var curPosition by remember { mutableLongStateOf(0L) }

    LaunchedEffect(key1 = true) {
        curPosition = storageHandler.playbackPositionState.value
    }

    BroadcastReceiver(action = Broadcast_CUR_POSITION_CHANGED) { _, intent ->
        curPosition = intent!!.getLongExtra(CUR_POSITION_ARG, 0)
    }

    Column(modifier.padding(horizontal = 10.dp)) {
        Slider(
            value = curPosition.toFloat(),
            valueRange = 0F..length.toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = lightVibrantColor,
                activeTrackColor = lightVibrantColor
            ),
            onValueChange = { curPosition = it.toLong() },
            onValueChangeFinished = {
                playingUIHandler.sendSeekToBroadcast(curPosition)
            }
        )

        Row(Modifier.fillMaxWidth()) {
            content(curPosition, length, lightVibrantColor)
        }
    }
}