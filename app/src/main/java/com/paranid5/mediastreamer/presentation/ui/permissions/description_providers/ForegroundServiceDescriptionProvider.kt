package com.paranid5.mediastreamer.presentation.ui.permissions.description_providers

import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
data class ForegroundServiceDescriptionProvider(override val description: String) :
    PermissionDescriptionProvider