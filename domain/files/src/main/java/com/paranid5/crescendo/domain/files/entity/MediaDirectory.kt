package com.paranid5.crescendo.domain.files.entity

@JvmInline
value class MediaDirectory(private val value: String) : CharSequence {
    override val length: Int
        get() = value.length

    override fun get(index: Int): Char = value[index]

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence =
        value.subSequence(startIndex, endIndex)

    override fun toString(): String = value
}
