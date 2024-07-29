package com.paranid5.crescendo.cache.data

import com.paranid5.crescendo.core.common.caching.Formats
import com.paranid5.crescendo.core.common.trimming.TrimRange
import com.paranid5.crescendo.domain.stream.CurrentMetadataSubscriber
import com.paranid5.crescendo.domain.stream.StreamRepository
import com.paranid5.crescendo.domain.stream.currentMetadataDurationMillisFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

internal interface CacheDialogDataSource : CurrentMetadataSubscriber {
    val trimOffsetMillisState: StateFlow<Long>
    val totalDurationMillisState: StateFlow<Long>
    val filenameState: StateFlow<String>
    val selectedSaveOptionIndexState: StateFlow<Int>

    fun setTrimOffsetMillis(trimOffsetMillis: Long)
    fun setTotalDurationMillis(totalDurationMillis: Long)
    fun setFilename(filename: String)
    fun setSelectedSaveOptionIndex(selectedSaveOptionIndex: Int)
}

internal class CacheDialogDataSourceImpl(streamRepository: StreamRepository) :
    CacheDialogDataSource, CurrentMetadataSubscriber by streamRepository {
    private val _trimOffsetMillisState by lazy {
        MutableStateFlow(0L)
    }

    override val trimOffsetMillisState by lazy {
        _trimOffsetMillisState.asStateFlow()
    }

    override fun setTrimOffsetMillis(trimOffsetMillis: Long) =
        _trimOffsetMillisState.update { trimOffsetMillis }

    private val _totalDurationMillisState by lazy {
        MutableStateFlow(0L)
    }

    override val totalDurationMillisState by lazy {
        _totalDurationMillisState.asStateFlow()
    }

    override fun setTotalDurationMillis(totalDurationMillis: Long) =
        _totalDurationMillisState.update { totalDurationMillis }

    private val _filenameState by lazy {
        MutableStateFlow("")
    }

    override val filenameState by lazy {
        _filenameState.asStateFlow()
    }

    override fun setFilename(filename: String) =
        _filenameState.update { filename }

    private val _selectedSaveOptionIndexState by lazy {
        MutableStateFlow(0)
    }

    override val selectedSaveOptionIndexState by lazy {
        _selectedSaveOptionIndexState.asStateFlow()
    }

    override fun setSelectedSaveOptionIndex(selectedSaveOptionIndex: Int) =
        _selectedSaveOptionIndexState.update { selectedSaveOptionIndex }
}

internal inline val CacheDialogDataSource.trimRangeFlow
    get() = combine(
        trimOffsetMillisState,
        trimDurationMillisFlow
    ) { trimOffsetMillis, trimDurationMillis ->
        TrimRange(
            startPointMillis = trimOffsetMillis,
            totalDurationMillis = trimDurationMillis
        )
    }

internal inline val CacheDialogDataSource.trimDurationMillisFlow
    get() = combine(
        totalDurationMillisState,
        currentMetadataDurationMillisFlow
    ) { totalDurationMillis, metaMillis ->
        when (totalDurationMillis) {
            0L -> metaMillis
            else -> totalDurationMillis
        }
    }

internal inline val CacheDialogDataSource.isCacheButtonClickableFlow
    get() = filenameState.map { it.isNotBlank() }

internal inline val CacheDialogDataSource.cacheFormatFlow
    get() = selectedSaveOptionIndexState.map { Formats.entries[it] }