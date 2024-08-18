package com.paranid5.crescendo.ui.foundation

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.paranid5.crescendo.utils.identity
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
@Immutable
sealed interface UiState<out T> : Parcelable {
    @Parcelize
    data object Initial : UiState<Nothing>

    @Parcelize
    data object Loading : UiState<Nothing>

    @Parcelize
    data class Refreshing<out T>(val innerState: UiState<T>) : UiState<T>

    @Parcelize
    data class Error(val errorMessage: String? = null) : UiState<Nothing>

    @Parcelize
    data object Success : UiState<Nothing>

    @Parcelize
    data class Data<out T>(val entity: @RawValue T) : UiState<T>
}

fun <T> UiState<T>.getOrNull() = when (this) {
    is UiState.Data -> entity

    is UiState.Refreshing -> (innerState as? UiState.Data)?.entity

    is UiState.Error,
    is UiState.Loading,
    is UiState.Initial,
    is UiState.Success -> null
}

fun <T> UiState<T>.getOrThrow(): T =
    requireNotNull(getOrNull()) { "Container with no State" }

fun <T> UiState<T>.getOrDefault(default: T): T =
    fold(ifPresent = ::identity, ifEmpty = { default })

fun <T, R> UiState<T>.map(func: (T) -> R): UiState<R> {
    tailrec fun transform(state: UiState<T>): UiState<R> = when (state) {
        is UiState.Data -> func(getOrThrow()).toUiState()
        is UiState.Refreshing -> transform(state.innerState)
        is UiState.Error -> UiState.Error(state.errorMessage)
        is UiState.Loading -> UiState.Loading
        is UiState.Success -> UiState.Success
        is UiState.Initial -> UiState.Initial
    }

    return when (this) {
        is UiState.Refreshing -> UiState.Refreshing(transform(innerState))
        else -> transform(this)
    }
}

inline fun <T, R> UiState<T>.fold(ifPresent: (T) -> R, ifEmpty: () -> R): R =
    getOrNull()?.let(ifPresent) ?: ifEmpty()

fun <D> D.toUiState() = UiState.Data(this)

fun <D> D?.toUiStateIfNotNull() =
    this?.toUiState() ?: UiState.Error()

fun Throwable.toUiState() = UiState.Error(this::class.qualifiedName)

inline val <T> UiState<T>.isInitialOrLoading
    get() = this is UiState.Initial || this is UiState.Loading

inline val <T> UiState<T>.isLoadingOrRefreshing
    get() = this is UiState.Initial || this is UiState.Refreshing

inline val <T> UiState<T>.isOk
    get() = this is UiState.Success || this is UiState.Data
