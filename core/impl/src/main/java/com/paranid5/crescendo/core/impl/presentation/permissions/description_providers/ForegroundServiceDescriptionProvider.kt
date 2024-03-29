package com.paranid5.crescendo.core.impl.presentation.permissions.description_providers

import android.os.Build
import androidx.annotation.RequiresApi

@JvmInline
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
value class ForegroundServiceDescriptionProvider(override val description: String) :
    PermissionDescriptionProvider