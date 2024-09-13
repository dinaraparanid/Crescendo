package com.paranid5.system.services.common

import androidx.media3.session.MediaSessionService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

abstract class MediaSuspendService : MediaSessionService() {
    private val job = SupervisorJob()
    val serviceScope = CoroutineScope(Dispatchers.Main + job)

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
