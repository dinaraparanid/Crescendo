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
import com.paranid5.crescendo.domain.services.video_cash_service.VideoCashResponse
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

        const val VIDEO_CASH_STATUS_ARG = "video_cash_status"
    }

    private val cashStatusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Cashing result is received")

            val status = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                    intent!!.getParcelableExtra(
                        VIDEO_CASH_STATUS_ARG,
                        VideoCashResponse::class.java
                    )

                else -> intent!!.getParcelableExtra(VIDEO_CASH_STATUS_ARG)
            }!!

            mOnVideoCashCompleted(status, applicationContext)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiverCompat(cashStatusReceiver, IntentFilter(Broadcast_VIDEO_CASH_COMPLETED))

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
                        curScreenState = viewModel.curScreenState,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(cashStatusReceiver)
    }

    internal fun mOnVideoCashCompleted(status: VideoCashResponse, context: Context) =
        Toast.makeText(
            context,
            when (status) {
                is VideoCashResponse.Error -> {
                    val (httpCode, description) = status
                    "${context.getString(R.string.error)} $httpCode: $description"
                }

                VideoCashResponse.Success ->
                    context.getString(R.string.video_cashed)

                VideoCashResponse.Canceled ->
                    context.getString(R.string.video_canceled)

                VideoCashResponse.AudioConversionError ->
                    context.getString(R.string.audio_conversion_error)

                VideoCashResponse.FileCreationError ->
                    context.getString(R.string.file_creation_error)

                VideoCashResponse.ConnectionLostError ->
                    context.getString(R.string.connection_lost)
            },
            Toast.LENGTH_LONG
        ).show()
}