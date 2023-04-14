package com.paranid5.mediastreamer.presentation.main_activity

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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.rememberNavController
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.domain.video_cash_service.VideoCashResponse
import com.paranid5.mediastreamer.presentation.NavHostController
import com.paranid5.mediastreamer.presentation.composition_locals.LocalActivity
import com.paranid5.mediastreamer.presentation.composition_locals.LocalMainViewModel
import com.paranid5.mediastreamer.presentation.composition_locals.LocalNavController
import com.paranid5.mediastreamer.presentation.streaming.VIDEO_CASH_STATUS_ARG
import com.paranid5.mediastreamer.presentation.ui.App
import com.paranid5.mediastreamer.presentation.ui.theme.MediaStreamerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val viewModel by viewModel<MainActivityViewModel>()

    companion object {
        private val TAG = MainActivity::class.simpleName!!

        private const val BROADCAST_LOCATION = "com.paranid5.mediastreamer.presentation"
        const val Broadcast_VIDEO_CASH_COMPLETED = "$BROADCAST_LOCATION.VIDEO_CASH_COMPLETED"
    }

    private val cashStatusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Cashing result is received")

            val status = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                    intent!!.getSerializableExtra(
                        VIDEO_CASH_STATUS_ARG,
                        VideoCashResponse::class.java
                    )!!

                else -> intent!!.getSerializableExtra(VIDEO_CASH_STATUS_ARG)!! as VideoCashResponse
            }

            mOnVideoCashCompleted(status, applicationContext)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(cashStatusReceiver, IntentFilter(Broadcast_VIDEO_CASH_COMPLETED))

        setContent {
            MediaStreamerTheme {
                val mainNavController = NavHostController(
                    value = rememberNavController(),
                    mainActivityViewModel = viewModel
                )

                CompositionLocalProvider(
                    LocalNavController provides mainNavController,
                    LocalActivity provides this,
                    LocalMainViewModel provides viewModel
                ) {
                    App(
                        curScreenState = viewModel.curScreenState,
                        streamScreenState = viewModel.streamScreenState
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(cashStatusReceiver)
    }

    internal fun mOnVideoCashCompleted(status: VideoCashResponse, context: Context) {
        val errorStringRes = context.getString(R.string.error)
        val successfulCashingStringRes = context.getString(R.string.video_cashed)
        val canceledStringRes = context.getString(R.string.video_canceled)

        Toast.makeText(
            context,
            when (status) {
                is VideoCashResponse.Error -> {
                    val (httpCode, description) = status
                    "$errorStringRes $httpCode: $description"
                }

                is VideoCashResponse.Success -> successfulCashingStringRes

                is VideoCashResponse.Canceled -> canceledStringRes
            },
            Toast.LENGTH_LONG
        ).show()
    }
}