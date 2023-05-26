package com.paranid5.mediastreamer.domain.stream_service

data class EqualizerData(
    val minBandLevel: Short,
    val maxBandLevel: Short,
    val bandLevels: ShortArray,
    val curPreset: Short,
    val presets: Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EqualizerData

        if (minBandLevel != other.minBandLevel) return false
        if (maxBandLevel != other.maxBandLevel) return false
        if (!bandLevels.contentEquals(other.bandLevels)) return false
        if (curPreset != other.curPreset) return false
        return presets.contentEquals(other.presets)
    }

    override fun hashCode(): Int {
        var result = minBandLevel.toInt()
        result = 31 * result + maxBandLevel
        result = 31 * result + bandLevels.contentHashCode()
        result = 31 * result + curPreset.hashCode()
        result = 31 * result + presets.contentHashCode()
        return result
    }
}
