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
import com.paranid5.mediastreamer.presentation.Screens
import com.paranid5.mediastreamer.presentation.StateChangedCallback
import com.paranid5.mediastreamer.presentation.composition_locals.LocalNavController
import com.paranid5.mediastreamer.presentation.ui.OnBackPressedHandler
import com.paranid5.mediastreamer.presentation.ui.OnUIStateChanged
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

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
private fun ConfirmButton(
    isConfirmButtonActive: Boolean,
    viewModel: SearchStreamViewModel,
    modifier: Modifier = Modifier
) {
    val navHostController = LocalNavController.current

    Button(
        enabled = isConfirmButtonActive,
        modifier = modifier,
        onClick = {
            viewModel.onConfirmUrlButtonPressed()
            navHostController.navigateIfNotSame(Screens.StreamScreen.Streaming)
        }
    ) {
        Text(stringResource(R.string.confirm))
    }
}

@Composable
fun SearchStreamScreen(
    viewModel: SearchStreamViewModel,
    curScreenState: MutableStateFlow<Screens>,
    modifier: Modifier = Modifier,
) {
    curScreenState.update { Screens.StreamScreen.Searching }

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
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 10.dp)
            )

            ConfirmButton(
                isConfirmButtonActive,
                viewModel,
                modifier = Modifier.wrapContentWidth().align(Alignment.CenterHorizontally)
            )
        }
    }
}