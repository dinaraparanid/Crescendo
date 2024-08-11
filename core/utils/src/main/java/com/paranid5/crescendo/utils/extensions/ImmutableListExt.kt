package com.paranid5.crescendo.utils.extensions

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalContracts::class, ExperimentalTypeInference::class)
inline fun <T> buildImmutableList(
    @BuilderInference builderAction: MutableList<T>.() -> Unit
): ImmutableList<T> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return persistentListOf<T>().mutate(builderAction)
}

fun <T> List<T>?.orNil() = this?.toImmutableList() ?: persistentListOf()

fun <T> ImmutableList<T>?.orNil() = this ?: persistentListOf()

operator fun <T> ImmutableList<T>.plus(elem: T) = buildImmutableList {
    addAll(this@plus)
    add(elem)
}
