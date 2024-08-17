package com.paranid5.crescendo.feature.stream.presentation.ui

//@Composable
//internal fun UrlEditor(
//    modifier: Modifier = Modifier,
//    viewModel: StreamViewModelImpl = koinViewModel(),
//) {
//    val currentText by viewModel.collectCurrentTextAsState()
//
//    AppOutlinedTextField(
//        value = currentText,
//        modifier = modifier,
//        label = { UrlEditorLabel() },
//        onValueChange = { viewModel.setCurrentText(currentText = it) },
//    )
//}
//
//@Composable
//private fun UrlEditorLabel(modifier: Modifier = Modifier) =
//    Text(
//        text = stringResource(R.string.enter_stream_url),
//        color = colors.primary,
//        style = typography.caption,
//        modifier = modifier,
//    )
