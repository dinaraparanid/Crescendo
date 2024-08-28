package com.paranid5.crescendo.system.services.track.media_session

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.core.media.images.getTrackCoverBitmapAsync
import com.paranid5.crescendo.system.services.track.TrackService
import com.paranid5.crescendo.utils.extensions.toAndroidMetadata
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal suspend fun TrackService.startMetadataMonitoring() =
    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        playerProvider
            .currentTrackFlow
            .distinctUntilChanged()
            .map { it?.toAndroidMetadata(context = this@startMetadataMonitoring) }
            .collectLatest { metadata ->
                metadata?.let(mediaSessionManager::updateMetadata)
            }
    }

private suspend inline fun Track.toAndroidMetadata(context: Context) =
    toAndroidMetadata(getTrackCoverBitmapAsync(context, path).await())
