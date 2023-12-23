package com.paranid5.crescendo.domain.metadata

import androidx.compose.runtime.Immutable
import com.paranid5.crescendo.domain.tracks.Track
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
@Immutable
data class AudioMetadata(
    override val title: String? = null,
    override val author: String? = null,
    @JvmField val album: String? = null,
    override val covers: List<String> = listOf(),
    override val durationMillis: Long = 0,
) : Metadata {
    companion object {
        fun extract(track: Track) = AudioMetadata(
            title = track.title,
            author = track.artist,
            album = track.album,
            covers = listOf(track.path),
            durationMillis = track.durationMillis
        )
    }
}
