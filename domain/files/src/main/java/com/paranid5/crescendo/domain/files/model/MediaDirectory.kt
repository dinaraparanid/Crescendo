package com.paranid5.crescendo.domain.files.model

@JvmInline
value class MediaDirectory(private val value: String) : CharSequence {
    override val length
        get() = value.length

    override fun get(index: Int) = value[index]

    override fun subSequence(startIndex: Int, endIndex: Int) =
        value.subSequence(startIndex, endIndex)
}