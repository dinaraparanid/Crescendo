package com.paranid5.system.services.common

import androidx.lifecycle.LifecycleService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

abstract class SuspendService : LifecycleService() {
    private val job = SupervisorJob()
    val serviceScope = CoroutineScope(Dispatchers.Main + job)

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}