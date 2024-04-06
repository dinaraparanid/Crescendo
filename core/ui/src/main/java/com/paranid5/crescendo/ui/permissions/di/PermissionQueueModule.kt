package com.paranid5.crescendo.ui.permissions.di

import android.os.Build
import com.paranid5.crescendo.core.impl.di.AUDIO_RECORDING_PERMISSION_QUEUE
import com.paranid5.crescendo.core.impl.di.EXTERNAL_STORAGE_PERMISSION_QUEUE
import com.paranid5.crescendo.core.impl.di.FOREGROUND_SERVICE_PERMISSION_QUEUE
import com.paranid5.crescendo.ui.permissions.audioRecordingPermissionQueue
import com.paranid5.crescendo.ui.permissions.externalStoragePermissionQueue
import com.paranid5.crescendo.ui.permissions.foregroundServicePermissionQueue
import org.koin.core.qualifier.named
import org.koin.dsl.module

val permissionQueuesModule = module {
    single(named(EXTERNAL_STORAGE_PERMISSION_QUEUE)) { externalStoragePermissionQueue }
    single(named(AUDIO_RECORDING_PERMISSION_QUEUE)) { audioRecordingPermissionQueue }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        single(named(FOREGROUND_SERVICE_PERMISSION_QUEUE)) { foregroundServicePermissionQueue }
}