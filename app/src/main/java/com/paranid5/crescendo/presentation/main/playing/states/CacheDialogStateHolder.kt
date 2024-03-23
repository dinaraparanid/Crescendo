package com.paranid5.crescendo.presentation.main.playing.states

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.states.stream.CurrentMetadataStateSubscriber
import com.paranid5.crescendo.data.states.stream.CurrentMetadataStateSubscriberImpl
import com.paranid5.crescendo.data.states.stream.currentMetadataDurationMillisFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

interface CacheDialogStateHolder : CurrentMetadataStateSubscriber {
    val trimOffsetMillisState: StateFlow<Long>
    val totalDurationMillisState: StateFlow<Long>
    val filenameState: StateFlow<String>
    val selectedSaveOptionIndexState: StateFlow<Int>

    fun setTrimOffsetMillis(trimOffsetMillis: Long)
    fun setTotalDurationMillis(totalDurationMillis: Long)
    fun setFilename(filename: String)
    fun setSelectedSaveOptionIndex(selectedSaveOptionIndex: Int)
}

class CacheDialogStateHolderImpl(storageRepository: StorageRepository) : CacheDialogStateHolder,
    CurrentMetadataStateSubscriber by CurrentMetadataStateSubscriberImpl(storageRepository) {
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

inline val CacheDialogStateHolder.trimRangeFlow
    get() = combine(
        trimOffsetMillisState,
        trimDurationMillisFlow
    ) { trimOffsetMillis, trimDurationMillis ->
        com.paranid5.crescendo.core.common.trimming.TrimRange(
            startPointMillis = trimOffsetMillis,
            totalDurationMillis = trimDurationMillis
        )
    }

inline val CacheDialogStateHolder.trimDurationMillisFlow
    get() = combine(
        totalDurationMillisState,
        currentMetadataDurationMillisFlow
    ) { totalDurationMillis, metaMillis ->
        when (totalDurationMillis) {
            0L -> metaMillis
            else -> totalDurationMillis
        }
    }

inline val CacheDialogStateHolder.isCacheButtonClickableFlow
    get() = filenameState.map { it.isNotBlank() }

inline val CacheDialogStateHolder.cacheFormatFlow
    get() = selectedSaveOptionIndexState.map { com.paranid5.crescendo.core.common.caching.Formats.entries[it] }