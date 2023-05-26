package com.paranid5.mediastreamer.domain.stream_service

data class EqualizerData(
    private val minBandLevel: Short,
    private val maxBandLevel: Short,
    private val bandLevels: ShortArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EqualizerData

        if (minBandLevel != other.minBandLevel) return false
        if (maxBandLevel != other.maxBandLevel) return false
        return bandLevels.contentEquals(other.bandLevels)
    }

    override fun hashCode(): Int {
        var result = minBandLevel.toInt()
        result = 31 * result + maxBandLevel
        result = 31 * result + bandLevels.contentHashCode()
        return result
    }
}
