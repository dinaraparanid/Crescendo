package com.paranid5.crescendo.feature.stream.presentation.ui

//@Composable
//internal fun PlayButton(modifier: Modifier = Modifier) {
//    val isForegroundServicePermissionDialogShownState = remember { mutableStateOf(false) }
//    val isAudioRecordingPermissionDialogShownState = remember { mutableStateOf(false) }
//
//    Box(modifier) {
//        val (areForegroundPermissionsGranted, launchFSPermissions) =
//            foregroundServicePermissionsRequestLauncherCompat(
//                isForegroundServicePermissionDialogShownState,
//                Modifier.align(Alignment.Center)
//            )
//
//        val (isRecordingPermissionGranted, launchRecordPermissions) =
//            audioRecordingPermissionsRequestLauncher(
//                isAudioRecordingPermissionDialogShownState,
//                Modifier.align(Alignment.Center)
//            )
//
//        PlayButtonImpl(
//            areForegroundPermissionsGranted = areForegroundPermissionsGranted,
//            isRecordingPermissionGranted = isRecordingPermissionGranted,
//            launchFSPermissions = launchFSPermissions,
//            launchRecordPermissions = launchRecordPermissions,
//            modifier = Modifier
//                .fillMaxWidth()
//                .align(Alignment.Center),
//        )
//    }
//}
//
//@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
//@Composable
//private inline fun PlayButtonImpl(
//    areForegroundPermissionsGranted: Boolean,
//    isRecordingPermissionGranted: Boolean,
//    crossinline launchFSPermissions: () -> Unit,
//    crossinline launchRecordPermissions: () -> Unit,
//    modifier: Modifier = Modifier,
//    viewModel: StreamViewModelImpl = koinViewModel(),
//    streamServiceAccessor: StreamServiceAccessor = koinInject()
//) {
//    val playingSheetState = LocalPlayingSheetState.current
//    val playingPagerState = LocalPlayingPagerState.current
//
//    val currentText by viewModel.collectCurrentTextAsState()
//    val isConfirmButtonActive by viewModel.collectIsConfirmButtonActiveAsState()
//    val coroutineScope = rememberCoroutineScope()
//
//    Button(
//        enabled = isConfirmButtonActive,
//        modifier = modifier,
//        colors = ButtonDefaults.buttonColors(
//            containerColor = colors.background.alternative,
//        ),
//        content = { ButtonLabel(stringResource(R.string.play)) },
//        onClick = {
//            when {
//                areForegroundPermissionsGranted.not() -> launchFSPermissions()
//
//                isRecordingPermissionGranted.not() -> launchRecordPermissions()
//
//                else -> coroutineScope.launch {
//                    startStreaming(
//                        publisher = viewModel,
//                        streamServiceAccessor = streamServiceAccessor,
//                        currentText = currentText,
//                        playingPagerState = playingPagerState,
//                        playingSheetState = playingSheetState,
//                    )
//                }
//            }
//        }
//    )
//}
