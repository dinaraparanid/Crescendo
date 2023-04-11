package com.paranid5.mediastreamer.presentation.search_stream

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.presentation.LocalNavController
import com.paranid5.mediastreamer.presentation.Screens
import com.paranid5.mediastreamer.presentation.StateChangedCallback
import com.paranid5.mediastreamer.presentation.composition_locals.LocalStreamState
import com.paranid5.mediastreamer.presentation.composition_locals.StreamStates
import com.paranid5.mediastreamer.presentation.ui.OnUIStateChanged
import com.paranid5.mediastreamer.presentation.ui.OnBackPressedHandler

@Composable
private fun Label(modifier: Modifier = Modifier) =
    Text(text = "${stringResource(R.string.enter_stream_url)}:", modifier = modifier)

@Composable
private fun UrlEditor(
    modifier: Modifier = Modifier,
    inputText: String?,
    hint: String = stringResource(R.string.your_url),
    viewModel: SearchStreamViewModel
) {
    TextField(
        value = inputText ?: "",
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
    val navHostController = LocalNavController.current
    val streamState = LocalStreamState.current

    Button(
        enabled = isConfirmButtonActive,
        onClick = {
            viewModel.onConfirmUrlButtonPressed()
            navHostController.navigateIfNotSame(Screens.StreamScreen.Streaming)
            streamState.value = StreamStates.STREAMING
        },
        modifier = Modifier
            .wrapContentWidth()
            .align(Alignment.CenterHorizontally)
    ) {
        Text(stringResource(R.string.confirm))
    }
}

@Composable
fun SearchStreamScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchStreamViewModel
) {
    LocalStreamState.current.value = StreamStates.STREAMING

    val orientation = LocalConfiguration.current.orientation

    val inputText by viewModel
        .presenter
        .currentTextState
        .collectAsState(initial = "")

    val isConfirmButtonActive by remember {
        derivedStateOf { inputText?.isNotEmpty() == true }
    }

    val lifecycleOwnerState by rememberUpdatedState(LocalLifecycleOwner.current)

    OnUIStateChanged(
        lifecycleOwner = lifecycleOwnerState,
        StateChangedCallback(
            uiHandler = viewModel.handler,
            state = viewModel.isConfirmButtonPressedState,
            onDispose = { viewModel.finishUrlSetting() }
        ) {
            startStreaming(url = inputText!!)
            viewModel.finishUrlSetting()
        }
    )

    OnBackPressedHandler { isStackEmpty ->
        if (isStackEmpty) {
            // TODO: Click twice to exit
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .let { mod ->
                when (orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> mod.padding(bottom = 15.dp)
                    else -> mod
                }
            }
    ) {
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