package com.paranid5.mediastreamer.presentation.appbar.stream_button

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.paranid5.mediastreamer.POST_NOTIFICATIONS_PERMISSION_QUEUE
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.presentation.LocalActivity
import com.paranid5.mediastreamer.presentation.LocalNavController
import com.paranid5.mediastreamer.presentation.Screens
import com.paranid5.mediastreamer.presentation.ui.extensions.openAppSettings
import com.paranid5.mediastreamer.presentation.ui.permissions.PermissionDialog
import com.paranid5.mediastreamer.presentation.ui.permissions.PostNotificationDescriptionProvider
import com.paranid5.mediastreamer.presentation.ui.theme.LocalAppColors
import org.koin.androidx.compose.get
import org.koin.core.qualifier.named
import java.util.Queue

@SuppressLint("NewApi")
@Composable
fun StreamButton(
    modifier: Modifier = Modifier,
    streamButtonUIHandler: StreamButtonUIHandler = get()
) {
    val activity = LocalActivity.current!!
    val navHostController = LocalNavController.current
    val currentScreenTitle by navHostController.currentRouteState.collectAsState()

    val isNotificationPermissionRequired = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    val isPostNotificationsPermissionDialogShownState = remember { mutableStateOf(false) }

    val postNotificationsPermission = when {
        isNotificationPermissionRequired -> get<Queue<String>>(
            named(POST_NOTIFICATIONS_PERMISSION_QUEUE)
        ).first()
        else -> null
    }

    val postNotificationsDescriptionProvider = when {
        isNotificationPermissionRequired -> get<PostNotificationDescriptionProvider>()
        else -> null
    }

    var isPostNotificationsPermissionGranted by remember {
        mutableStateOf(
            postNotificationsPermission?.let { permission ->
                ContextCompat.checkSelfPermission(activity, permission) ==
                        PackageManager.PERMISSION_GRANTED
            } ?: true
        )
    }

    val notificationsPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isPostNotificationsPermissionGranted = it }

    Box(modifier) {
        FloatingActionButton(
            modifier = modifier,
            onClick = {
                when {
                    isPostNotificationsPermissionGranted -> streamButtonUIHandler.navigateToStream(
                        navHostController,
                        currentScreenTitle
                    )

                    else -> {
                        notificationsPermissionResultLauncher.launch(postNotificationsPermission)
                        isPostNotificationsPermissionDialogShownState.value = true
                    }
                }
            }
        ) {
            if (currentScreenTitle == Screens.StreamScreen.Streaming.title)
                Icon(
                    painter = painterResource(id = R.drawable.search_icon),
                    contentDescription = stringResource(id = R.string.home),
                    tint = LocalAppColors.current.value.primary,
                    modifier = Modifier.size(30.dp)
                )
            else
                Icon(
                    painter = painterResource(id = R.drawable.stream_icon),
                    contentDescription = stringResource(id = R.string.home),
                    tint = LocalAppColors.current.value.primary,
                    modifier = Modifier.size(30.dp)
                )
        }

        if (isPostNotificationsPermissionDialogShownState.value)
            if (!isPostNotificationsPermissionGranted)
                PermissionDialog(
                    isDialogShownState = isPostNotificationsPermissionDialogShownState,
                    modifier = Modifier.align(Alignment.Center),
                    permissionDescriptionProvider = postNotificationsDescriptionProvider!!,
                    isPermanentlyDeclined = !activity.shouldShowRequestPermissionRationale(
                        postNotificationsPermission!!
                    ),
                    onGrantedClicked = {
                        notificationsPermissionResultLauncher.launch(postNotificationsPermission)
                    },
                    onGoToAppSettingsClicked = activity::openAppSettings
                )
    }
}