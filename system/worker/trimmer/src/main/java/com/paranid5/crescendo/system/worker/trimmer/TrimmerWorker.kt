package com.paranid5.crescendo.system.worker.trimmer

import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import arrow.core.Either
import com.paranid5.crescendo.core.media.files.MediaFile
import com.paranid5.crescendo.core.media.files.trimmedCatching
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.domain.metadata.MetadataExtractor
import com.paranid5.crescendo.domain.tags.TagsRepository
import com.paranid5.crescendo.system.receivers.TrimmingStatusReceiver
import com.paranid5.crescendo.utils.extensions.sendAppBroadcast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

class TrimmerWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext = context, params = params), KoinComponent {
    companion object {
        const val REQUEST_KEY = "TRIMMER_WORK_REQUEST"
    }

    private val json by inject<Json>()
    private val metadataExtractor by inject<MetadataExtractor>()
    private val tagsRepository by inject<TagsRepository>()

    override suspend fun doWork(): Result = Either.catch {
        trimTrackAndSendBroadcast(
            context = applicationContext,
            request = json.decodeFromString<TrimmerWorkRequest>(
                requireNotNull(inputData.getString(REQUEST_KEY))
            ),
        )
    }.fold(
        ifLeft = { Result.failure() },
        ifRight = { Result.success() },
    )

    private suspend inline fun trimTrackAndSendBroadcast(
        context: Context,
        request: TrimmerWorkRequest,
    ) = context.sendTrimmingStatusBroadcast(
        withContext(Dispatchers.IO) { trimTrackResult(request = request) }
    )

    private suspend inline fun trimTrackResult(
        request: TrimmerWorkRequest,
    ) = MediaFile.AudioFile(File(request.track.path))
        .trimmedCatching(
            outputFilename = request.outputFilename,
            audioFormat = request.audioFormat,
            trimRange = request.trimRange,
            pitchAndSpeed = request.pitchAndSpeed,
            fadeDurations = request.fadeDurations,
        )
        .onRight { file ->
            tagsRepository.setAudioTags(
                audioFile = file,
                metadata = metadataExtractor.extractAudioMetadata(request.track),
                audioFormat = request.audioFormat,
            )
        }

    private fun Context.sendTrimmingStatusBroadcast(trimmingResult: Either<Throwable, MediaFile.AudioFile>) =
        sendAppBroadcast(
            Intent(applicationContext, TrimmingStatusReceiver::class.java)
                .setAction(TrimmingStatusReceiver.Broadcast_TRIMMING_COMPLETED)
                .putExtra(
                    TrimmingStatusReceiver.TRIMMING_STATUS_ARG,
                    trimmingStatusMessage(trimmingResult),
                )
        )

    private fun Context.trimmingStatusMessage(trimmingResult: Either<Throwable, MediaFile.AudioFile>) =
        when (trimmingResult) {
            is Either.Right -> getString(R.string.trimmer_done)

            is Either.Left -> "${getString(R.string.error)}: " +
                    (trimmingResult.value.message
                        ?: getString(R.string.unknown_error))
        }
}
