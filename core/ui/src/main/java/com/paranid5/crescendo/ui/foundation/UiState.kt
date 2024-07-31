package com.paranid5.crescendo.ui.foundation

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
sealed interface UiState<out T : Parcelable> : Parcelable {
    @Parcelize
    data object Undefined : UiState<Nothing>

    @Parcelize
    data object Loading : UiState<Nothing>

    @Parcelize
    data class Refreshing<T : Parcelable>(val innerState: UiState<T>) : UiState<T>

    @Parcelize
    data class Error(val errorMessage: String? = null) : UiState<Nothing>

    @Parcelize
    data object Success : UiState<Nothing>

    @Parcelize
    data class Data<T : Parcelable>(val entity: T) : UiState<T>
}

fun <T : Parcelable> UiState<T>.getOrNull() = when (this) {
    is UiState.Data -> entity

    is UiState.Refreshing -> (innerState as? UiState.Data)?.entity

    is UiState.Error,
    is UiState.Loading,
    is UiState.Undefined,
    is UiState.Success -> null
}

fun <T : Parcelable> UiState<T>.getOrThrow(): T {
    check(this is UiState.Data<T> || this is UiState.Refreshing<T>) {
        "Only Data and Refreshing states are allowed, this state is $this"
    }

    return requireNotNull(getOrNull()) { "Container with no State" }
}

fun <T : Parcelable, R : Parcelable> UiState<T>.map(func: T.() -> R): UiState<R> {
    tailrec fun impl(state: UiState<T>): UiState<R> = when (state) {
        is UiState.Data -> func(getOrThrow()).toUiState()
        is UiState.Refreshing -> impl(state.innerState)
        is UiState.Error -> UiState.Error(state.errorMessage)
        is UiState.Loading -> UiState.Loading
        is UiState.Success -> UiState.Success
        is UiState.Undefined -> UiState.Undefined
    }

    return when (this) {
        is UiState.Refreshing -> UiState.Refreshing(impl(innerState))
        else -> impl(this)
    }
}

fun <D : Parcelable> D.toUiState() = UiState.Data(this)

fun <D : Parcelable> D?.toUiStateIfNotNull() =
    this?.toUiState() ?: UiState.Error()

fun Throwable.toUiState() = UiState.Error(this::class.qualifiedName)

inline val <T : Parcelable> UiState<T>.isUndefinedOrLoading
    get() = this is UiState.Undefined || this is UiState.Loading

inline val <T : Parcelable> UiState<T>.isLoadingOrRefreshing
    get() = this is UiState.Undefined || this is UiState.Refreshing

inline val <T : Parcelable> UiState<T>.isOk
    get() = this is UiState.Success || this is UiState.Data
