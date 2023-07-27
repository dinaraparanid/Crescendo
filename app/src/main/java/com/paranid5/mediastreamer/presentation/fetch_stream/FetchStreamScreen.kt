package com.paranid5.mediastreamer.presentation.fetch_stream

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.domain.StorageHandler
import com.paranid5.mediastreamer.presentation.Screens
import com.paranid5.mediastreamer.presentation.StateChangedCallback
import com.paranid5.mediastreamer.presentation.composition_locals.LocalNavController
import com.paranid5.mediastreamer.presentation.ui.AudioStatus
import com.paranid5.mediastreamer.presentation.ui.OnUIStateChanged
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

private val youtubeUrlRegex = Regex("https://(www\\.youtube\\.com/watch\\?v=|youtu\\.be/)\\S{11}")

@Composable
fun SearchStreamScreen(
    viewModel: FetchStreamViewModel,
    curScreenState: MutableStateFlow<Screens>,
    modifier: Modifier = Modifier
) {
    curScreenState.update { Screens.StreamFetching }

    val orientation = LocalConfiguration.current.orientation

    val inputText by viewModel
        .presenter
        .currentTextState
        .collectAsState(initial = "")

    val isConfirmButtonActive by remember {
        derivedStateOf { inputText?.matches(youtubeUrlRegex) == true }
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

            ConfirmButton(
                isConfirmButtonActive,
                viewModel,
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun Label(modifier: Modifier = Modifier) =
    Text(text = "${stringResource(R.string.enter_stream_url)}:", modifier = modifier)

@Composable
private fun UrlEditor(
    modifier: Modifier = Modifier,
    inputText: String?,
    hint: String = stringResource(R.string.your_url),
    viewModel: FetchStreamViewModel
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
    viewModel: FetchStreamViewModel,
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = koinInject()
) {
    val navHostController = LocalNavController.current
    val scope = rememberCoroutineScope()

    Button(
        enabled = isConfirmButtonActive,
        modifier = modifier,
        onClick = {
            scope.launch {
                storageHandler.storeAudioStatus(AudioStatus.STREAMING)
                viewModel.onConfirmUrlButtonPressed()
                navHostController.navigateIfNotSame(Screens.Audio.Playing)
            }
        }
    ) {
        Text(stringResource(R.string.confirm))
    }
}