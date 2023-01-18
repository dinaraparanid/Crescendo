package com.paranid5.mediastreamer.presentation.ui_handlers

import android.app.Service
import android.content.Intent
import android.os.Build
import com.paranid5.mediastreamer.MainApplication
import com.paranid5.mediastreamer.StreamService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SearchStreamUIHandler : UIHandler, KoinComponent {
    private val application by inject<MainApplication>()

    private inline val appContext
        get() = application.applicationContext

    private fun Intent.putStreamUrlIfNotNull(url: String?) = apply {
        if (url != null) putExtra(StreamService.URL_ARG, url)
    }

    private fun startStreamService(url: String?) {
        val serviceIntent = Intent(appContext, StreamService::class.java)
            .putStreamUrlIfNotNull(url)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            appContext.startForegroundService(serviceIntent)
        else
            appContext.startService(serviceIntent)

        appContext.bindService(
            serviceIntent,
            application.streamServiceConnection,
            Service.BIND_AUTO_CREATE
        )
    }

    private fun switchToNextStream(url: String?) = application.sendBroadcast(
        Intent(StreamService.Broadcast_SWITCH).putStreamUrlIfNotNull(url)
    )

    private fun launchStreamService(url: String?) = when {
        !application.isStreamServiceConnected -> startStreamService(url)
        else -> switchToNextStream(url)
    }

    fun startStreaming(url: String?) {
        // TODO: streaming screen
        launchStreamService(url)
    }
}