package com.paranid5.mediastreamer.presentation.streaming

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.StorageHandler
import com.paranid5.mediastreamer.presentation.BroadcastReceiver
import com.paranid5.mediastreamer.presentation.ui.theme.LocalAppColors
import com.paranid5.mediastreamer.video_cash_service.VideoCashResponse
import org.koin.androidx.compose.get

@Composable
fun UtilsButtons(modifier: Modifier = Modifier) =
    Row(modifier.fillMaxWidth()) {
        EqualizerButton(Modifier.weight(1F))
        RepeatButton(Modifier.weight(1F))
        LikeButton(Modifier.weight(1F))
        DownloadButton(Modifier.weight(1F))
    }

@Composable
private fun EqualizerButton(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current.value

    IconButton(modifier = modifier, onClick = { /*TODO Equalizer*/ }) {
        Icon(
            modifier = Modifier.size(30.dp),
            painter = painterResource(R.drawable.equalizer),
            contentDescription = stringResource(R.string.equalizer),
            tint = colors.primary
        )
    }
}

@Composable
private fun RepeatButton(
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = get(),
    streamingUIHandler: StreamingUIHandler = get()
) {
    val colors = LocalAppColors.current.value
    var isRepeating by remember { mutableStateOf(storageHandler.isRepeatingState.value) }

    BroadcastReceiver(action = Broadcast_IS_REPEATING_CHANGED) { _, intent ->
        isRepeating = intent!!.getBooleanExtra(IS_REPEATING_ARG, false)
    }

    IconButton(modifier = modifier, onClick = { streamingUIHandler.sendChangeRepeatBroadcast() }) {
        Icon(
            modifier = Modifier.size(30.dp),
            painter = painterResource(if (isRepeating) R.drawable.repeat else R.drawable.no_repeat),
            contentDescription = stringResource(R.string.change_repeat),
            tint = colors.primary
        )
    }
}

@Composable
private fun LikeButton(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current.value
    var isLiked by remember { mutableStateOf(false) }

    BroadcastReceiver(action = Broadcast_IS_REPEATING_CHANGED) { _, intent ->
        // TODO: favourite database
    }

    /** TODO: favourite database */
    IconButton(modifier = modifier, onClick = { /** TODO: favourite database */ }) {
        Icon(
            modifier = Modifier.size(30.dp),
            painter = painterResource(if (isLiked) R.drawable.like_filled else R.drawable.like),
            contentDescription = stringResource(R.string.favourites),
            tint = colors.primary
        )
    }
}

@Composable
private fun DownloadButton(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current.value
    val isCashPropertiesDialogShownState = remember { mutableStateOf(false) }

    BroadcastReceiver(action = Broadcast_VIDEO_CASH_COMPLETED) { context, intent ->
        val status = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                intent!!.getSerializableExtra(VIDEO_CASH_STATUS, VideoCashResponse::class.java)!!
            else ->
                intent!!.getSerializableExtra(VIDEO_CASH_STATUS)!! as VideoCashResponse
        }

        onVideoCashCompleted(status, context!!)
    }

    Box(modifier) {
        IconButton(
            modifier = modifier,
            onClick = { isCashPropertiesDialogShownState.value = true }
        ) {
            Icon(
                modifier = Modifier.size(30.dp),
                painter = painterResource(R.drawable.save_icon),
                contentDescription = stringResource(R.string.download_as_mp3),
                tint = colors.primary
            )
        }

        if (isCashPropertiesDialogShownState.value)
            CashPropertiesDialog(isCashPropertiesDialogShownState)
    }
}

private fun onVideoCashCompleted(status: VideoCashResponse, context: Context) {
    val errorStringRes = context.getString(R.string.error)
    val successfulCashingStringRes = context.getString(R.string.video_cashed)

    Toast.makeText(
        context,
        when (status) {
            is VideoCashResponse.Error -> {
                val (httpCode, description) = status
                "$errorStringRes $httpCode: $description"
            }

            VideoCashResponse.Success -> successfulCashingStringRes
        },
        Toast.LENGTH_LONG
    ).show()
}