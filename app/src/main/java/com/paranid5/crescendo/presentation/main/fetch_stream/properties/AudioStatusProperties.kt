package com.paranid5.crescendo.presentation.main.fetch_stream.properties

import com.paranid5.crescendo.presentation.main.fetch_stream.FetchStreamViewModel

suspend fun FetchStreamViewModel.resetAudioStatusToStreaming() =
    audioStatusStateHolder.resetAudioStatusToStreaming()