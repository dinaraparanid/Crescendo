package com.paranid5.crescendo.presentation.main_activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.paranid5.crescendo.R
import com.paranid5.crescendo.domain.services.video_cache_service.VideoCacheResponse
import com.paranid5.crescendo.domain.utils.extensions.registerReceiverCompat
import com.paranid5.crescendo.presentation.NavHostController
import com.paranid5.crescendo.presentation.composition_locals.LocalActivity
import com.paranid5.crescendo.presentation.composition_locals.LocalNavController
import com.paranid5.crescendo.presentation.ui.App
import com.paranid5.crescendo.presentation.ui.theme.MediaStreamerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val viewModel by viewModel<MainActivityViewModel>()

    companion object {
        private val TAG = MainActivity::class.simpleName!!

        private const val BROADCAST_LOCATION = "com.paranid5.mediastreamer.presentation"
        const val Broadcast_VIDEO_CASH_COMPLETED = "$BROADCAST_LOCATION.VIDEO_CASH_COMPLETED"
        const val Broadcast_STREAMING_ERROR = "$BROADCAST_LOCATION.STREAMING_ERROR"

        const val VIDEO_CASH_STATUS_ARG = "video_cash_status"
        const val STREAMING_ERROR_ARG = "streaming_error"
    }

    private val cashStatusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            Log.d(TAG, "Cashing result is received")

            @Suppress("DEPRECATION")
            val status = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                    intent.getParcelableExtra(
                        VIDEO_CASH_STATUS_ARG,
                        VideoCacheResponse::class.java
                    )

                else -> intent.getParcelableExtra(VIDEO_CASH_STATUS_ARG)
            }!!

            mOnVideoCashCompleted(status, applicationContext)
        }
    }

    private val streamingErrorReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            Log.d(TAG, "Streaming error")
            val error = intent.getStringExtra(STREAMING_ERROR_ARG)!!
            Toast.makeText(this@MainActivity, error, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceivers()

        setContent {
            MediaStreamerTheme {
                val mainNavController = NavHostController(
                    value = rememberNavController(),
                    mainActivityViewModel = viewModel
                )

                CompositionLocalProvider(
                    LocalNavController provides mainNavController,
                    LocalActivity provides this,
                ) {
                    App(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceivers()
    }

    private fun registerReceivers() {
        registerReceiverCompat(cashStatusReceiver, IntentFilter(Broadcast_VIDEO_CASH_COMPLETED))
        registerReceiverCompat(streamingErrorReceiver, IntentFilter(Broadcast_STREAMING_ERROR))
    }

    private fun unregisterReceivers() {
        unregisterReceiver(cashStatusReceiver)
        unregisterReceiver(streamingErrorReceiver)
    }

    internal fun mOnVideoCashCompleted(status: VideoCacheResponse, context: Context) =
        Toast.makeText(
            context,
            when (status) {
                is VideoCacheResponse.Error -> {
                    val (httpCode, description) = status
                    "${context.getString(R.string.error)} $httpCode: $description"
                }

                VideoCacheResponse.Success ->
                    context.getString(R.string.video_cached)

                VideoCacheResponse.Canceled ->
                    context.getString(R.string.video_canceled)

                VideoCacheResponse.AudioConversionError ->
                    context.getString(R.string.audio_conversion_error)

                VideoCacheResponse.FileCreationError ->
                    context.getString(R.string.file_creation_error)

                VideoCacheResponse.ConnectionLostError ->
                    context.getString(R.string.connection_lost)

                VideoCacheResponse.LiveStreamNotAllowed ->
                    context.getString(R.string.livestream_not_cache)
            },
            Toast.LENGTH_LONG
        ).show()
}