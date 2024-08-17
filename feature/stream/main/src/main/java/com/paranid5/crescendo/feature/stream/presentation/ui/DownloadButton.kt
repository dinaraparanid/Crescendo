package com.paranid5.crescendo.feature.stream.presentation.ui

//@Composable
//internal fun DownloadButton(
//    modifier: Modifier = Modifier,
//    viewModel: StreamViewModelImpl = koinViewModel(),
//) {
//    val text by viewModel.collectCurrentTextAsState()
//
//    val isCachePropertiesDialogShownState = remember { mutableStateOf(false) }
//    var isCachePropertiesDialogShown by isCachePropertiesDialogShownState
//
//    Box(modifier) {
//        val (areStoragePermissionsGranted, launchStoragePermissions) =
//            externalStoragePermissionsRequestLauncher(
//                isExternalStoragePermissionDialogShownState = isCachePropertiesDialogShownState,
//                modifier = Modifier.align(Alignment.Center),
//            )
//
//        if (isCachePropertiesDialogShown && areStoragePermissionsGranted)
//            CacheDialog(
//                url = text,
//                modifier = Modifier.align(Alignment.Center),
//                hide = { isCachePropertiesDialogShown = false },
//            )
//
//        DownloadButtonImpl(
//            isCachePropertiesDialogShownState = isCachePropertiesDialogShownState,
//            areStoragePermissionsGranted = areStoragePermissionsGranted,
//            launchStoragePermissions = launchStoragePermissions,
//            modifier = Modifier
//                .fillMaxWidth()
//                .align(Alignment.Center),
//        )
//    }
//}
//
//@Composable
//private inline fun DownloadButtonImpl(
//    isCachePropertiesDialogShownState: MutableState<Boolean>,
//    areStoragePermissionsGranted: Boolean,
//    crossinline launchStoragePermissions: () -> Unit,
//    modifier: Modifier = Modifier,
//    viewModel: StreamViewModelImpl = koinViewModel(),
//) {
//    val isConfirmButtonActive by viewModel.collectIsConfirmButtonActiveAsState()
//    var isCachePropertiesDialogShown by isCachePropertiesDialogShownState
//
//    Button(
//        enabled = isConfirmButtonActive,
//        modifier = modifier,
//        colors = ButtonDefaults.buttonColors(
//            containerColor = colors.background.alternative,
//        ),
//        content = { ButtonLabel(stringResource(R.string.download)) },
//        onClick = {
//            when {
//                areStoragePermissionsGranted.not() -> launchStoragePermissions()
//                else -> isCachePropertiesDialogShown = true
//            }
//        }
//    )
//}
