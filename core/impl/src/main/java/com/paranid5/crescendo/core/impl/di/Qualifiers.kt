package com.paranid5.crescendo.core.impl.di

import android.os.Build
import androidx.annotation.RequiresApi

const val STREAM_WITH_NO_NAME = "stream_no_name"
const val UNKNOWN_STREAMER = "unknown_streamer"
const val IS_PLAYING = "is_playing_state"
const val AUDIO_SESSION_ID = "audio_session_id"
const val EQUALIZER_DATA = "equalizer_data"

const val STREAM_SERVICE_CONNECTION = "stream_service_connection"
const val TRACK_SERVICE_CONNECTION = "track_service_connection"
const val VIDEO_CACHE_SERVICE_CONNECTION = "video_cache_service_connection"

const val EXTERNAL_STORAGE_PERMISSION_QUEUE = "external_storage_permission_queue"

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
const val FOREGROUND_SERVICE_PERMISSION_QUEUE = "foreground_service_permission_queue"

const val AUDIO_RECORDING_PERMISSION_QUEUE = "audio_recording_permission_queue"