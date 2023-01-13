package com.paranid5.mediastreamer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.StateChangedCallback
import com.paranid5.mediastreamer.ui.OnUIStateChanged
import com.paranid5.mediastreamer.view_models.SearchStreamViewModel

@Composable
private fun Label(modifier: Modifier = Modifier) =
    Text(text = "${stringResource(R.string.enter_stream_url)}:", modifier = modifier)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UrlEditor(
    modifier: Modifier = Modifier,
    inputText: State<String?>,
    hint: String = stringResource(R.string.your_url),
    viewModel: SearchStreamViewModel
) {
    TextField(
        value = inputText.value ?: "",
        singleLine = true,
        placeholder = { Text(hint) },
        onValueChange = { viewModel.presenter.currentTextState.value = it },
        modifier = modifier.width(300.dp),
    )
}

@Composable
private fun ColumnScope.ConfirmButton(
    isConfirmButtonActive: Boolean,
    viewModel: SearchStreamViewModel
) {
    Button(
        enabled = isConfirmButtonActive,
        onClick = viewModel::onConfirmUrlButtonPressed,
        modifier = Modifier.wrapContentWidth().align(Alignment.CenterHorizontally)
    ) {
        Text(stringResource(R.string.confirm))
    }
}

@Composable
fun SearchStreamScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchStreamViewModel
) {
    val inputText = viewModel
        .presenter
        .currentTextState
        .collectAsState(initial = "")

    val isConfirmButtonActive by remember {
        derivedStateOf { inputText.value?.isNotEmpty() == true }
    }

    val lifecycleOwnerState = rememberUpdatedState(LocalLifecycleOwner.current)

    OnUIStateChanged(
        lifecycleOwner = lifecycleOwnerState.value,
        StateChangedCallback(
            uiHandler = viewModel.handler,
            state = viewModel.isConfirmButtonPressedState,
            onDispose = { viewModel.finishUrlSetting() }
        ) {
            startStreaming(url = inputText.value!!)
            viewModel.finishUrlSetting()
        }
    )

    Box(modifier.fillMaxSize()) {
        Column(Modifier.align(Alignment.Center)) {
            Label(Modifier.padding(bottom = 10.dp))

            UrlEditor(
                inputText = inputText,
                viewModel = viewModel,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 10.dp)
            )

            ConfirmButton(isConfirmButtonActive, viewModel)
        }
    }
}