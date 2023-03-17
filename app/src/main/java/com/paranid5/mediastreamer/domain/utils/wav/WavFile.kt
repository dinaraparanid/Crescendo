package com.paranid5.mediastreamer.domain.utils.wav

import java.io.*

typealias Sample = Long

class WavFile : Closeable {
    companion object {
        private const val BUFFER_SIZE = 4096
        private const val FMT_CHUNK_ID = 0x20746D66
        private const val DATA_CHUNK_ID = 0x61746164
        private const val RIFF_CHUNK_ID = 0x46464952
        private const val RIFF_TYPE_ID = 0x45564157

        fun createNewFile(
            file: File?,
            numChannels: Int,
            numFrames: Long,
            validBits: Int,
            sampleRate: Long
        ): WavFile {
            // Sanity check arguments

            if (numChannels < 1 || numChannels > 65535)
                throw Exception("Illegal number of channels, valid range 1 to 65536")

            if (numFrames < 0)
                throw Exception("Number of frames must be positive")

            if (validBits < 2 || validBits > 65535)
                throw Exception("Illegal number of valid bits, valid range 2 to 65536")

            if (sampleRate < 0)
                throw Exception("Sample rate must be positive")

            val wavFile = WavFile().apply { ->
                this.file = file
                this.numChannels = numChannels
                this.numFrames = numFrames
                this.sampleRate = sampleRate
                this.bytesPerSample = (validBits + 7) / 8
                this.blockAlign = this.bytesPerSample * numChannels
                this.validBits = validBits
                this.outStream = FileOutputStream(file)
            }

            // Calculate the chunk sizes
            val dataChunkSize = wavFile.blockAlign * numFrames

            var mainChunkSize = 4 +    // Riff Type
                    8 +                     // Format ID and size
                    16 +                    // Format data
                    8 +                     // Data ID and size
                    dataChunkSize

            // Chunks must be word aligned, so
            // if odd number of audio data bytes
            // adjust the main chunk size

            wavFile.isWordAlignAdjust = when (dataChunkSize % 2) {
                1L -> {
                    ++mainChunkSize
                    true
                }

                else -> false
            }

            // Set the main chunk size
            putLE(RIFF_CHUNK_ID.toLong(), wavFile.buffer, 0, 4)
            putLE(mainChunkSize, wavFile.buffer, 4, 4)
            putLE(RIFF_TYPE_ID.toLong(), wavFile.buffer, 8, 4)

            // Write out the header
            wavFile.outStream!!.write(wavFile.buffer, 0, 12)

            // Put format data in buffer
            val averageBytesPerSecond = sampleRate * wavFile.blockAlign
            putLE(FMT_CHUNK_ID.toLong(), wavFile.buffer, 0, 4) // Chunk ID
            putLE(16, wavFile.buffer, 4, 4) // Chunk Data Size
            putLE(1, wavFile.buffer, 8, 2) // Compression Code (Uncompressed)
            putLE(numChannels.toLong(), wavFile.buffer, 10, 2) // Number of channels
            putLE(sampleRate, wavFile.buffer, 12, 4) // Sample Rate
            putLE(averageBytesPerSecond, wavFile.buffer, 16, 4) // Average Bytes Per Second
            putLE(wavFile.blockAlign.toLong(), wavFile.buffer, 20, 2) // Block Align
            putLE(validBits.toLong(), wavFile.buffer, 22, 2) // Valid Bits

            // Write Format Chunk
            wavFile.outStream!!.write(wavFile.buffer, 0, 24)

            // Start Data Chunk
            putLE(DATA_CHUNK_ID.toLong(), wavFile.buffer, 0, 4) // Chunk ID
            putLE(dataChunkSize, wavFile.buffer, 4, 4) // Chunk Data Size

            // Write Format Chunk
            wavFile.outStream!!.write(wavFile.buffer, 0, 8)

            // Calculate the scaling factor for converting to a normalised double
            if (wavFile.validBits > 8) {
                // If more than 8 validBits, data is signed
                // Conversion required multiplying by magnitude of max positive value
                wavFile.floatOffset = 0.0
                wavFile.floatScale = (Long.MAX_VALUE shr 64 - wavFile.validBits).toDouble()
            } else {
                // Else if 8 or less validBits, data is unsigned
                // Conversion required dividing by max positive value
                wavFile.floatOffset = 1.0
                wavFile.floatScale = 0.5 * ((1 shl wavFile.validBits) - 1)
            }

            // Finally, set the IO State
            wavFile.bufferPointer = 0
            wavFile.bytesReadNum = 0
            wavFile.frameCounter = 0
            wavFile.ioState = IOState.WRITING
            return wavFile
        }

        fun createNewFileCatching(
            file: File?,
            numChannels: Int,
            numFrames: Long,
            validBits: Int,
            sampleRate: Long
        ) = runCatching { createNewFile(file, numChannels, numFrames, validBits, sampleRate) }

        fun openWavFile(file: File): WavFile {
            val wavFile = WavFile().apply {
                this.file = file
                this.inStream = FileInputStream(file)
            }

            // Read the first 12 bytes of the file
            var bytesRead = wavFile.inStream!!.read(wavFile.buffer, 0, 12)
            if (bytesRead != 12) throw Exception("Not enough wav file bytes for header")

            // Extract parts from the header
            val riffChunkID = getLE(wavFile.buffer, 0, 4)
            var chunkSize = getLE(wavFile.buffer, 4, 4)
            val riffTypeID = getLE(wavFile.buffer, 8, 4)

            // Check the header bytes contains the correct signature

            if (riffChunkID != RIFF_CHUNK_ID.toLong())
                throw Exception("Invalid Wav Header data, incorrect riff chunk ID")

            if (riffTypeID != RIFF_TYPE_ID.toLong())
                throw Exception("Invalid Wav Header data, incorrect riff type ID")

            // Check that the file size matches the number of bytes listed in header
            if (file.length() != chunkSize + 8)
                throw Exception("Header chunk size ($chunkSize) does not match file size (${file.length()})")

            var isFormatFound = false
            var isDataFound = false

            // Search for the Format and Data Chunks
            while (true) {
                // Read the first 8 bytes of the chunk (ID and chunk size)
                bytesRead = wavFile.inStream!!.read(wavFile.buffer, 0, 8)

                if (bytesRead == -1) throw Exception("Reached end of file without finding format chunk")
                if (bytesRead != 8) throw Exception("Could not read chunk header")

                // Extract the chunk ID and Size
                val chunkID = getLE(wavFile.buffer, 0, 4)
                chunkSize = getLE(wavFile.buffer, 4, 4)

                // Word align the chunk size
                // chunkSize specifies the number of bytes holding data. However,
                // the data should be word aligned (2 bytes) so we need to calculate
                // the actual number of bytes in the chunk

                var numChunkBytes = if (chunkSize % 2 == 1L) chunkSize + 1 else chunkSize

                when (chunkID) {
                    FMT_CHUNK_ID.toLong() -> {
                        // Flag that the format chunk has been found
                        isFormatFound = true

                        // Read in the header info
                        bytesRead = wavFile.inStream!!.read(wavFile.buffer, 0, 16)

                        // Check this is uncompressed data
                        val compressionCode = getLE(wavFile.buffer, 0, 2).toInt()
                        if (compressionCode != 1) throw Exception("Compression Code $compressionCode not supported")

                        // Extract the format information
                        wavFile.numChannels = getLE(wavFile.buffer, 2, 2).toInt()
                        wavFile.sampleRate = getLE(wavFile.buffer, 4, 4)
                        wavFile.blockAlign = getLE(wavFile.buffer, 12, 2).toInt()
                        wavFile.validBits = getLE(wavFile.buffer, 14, 2).toInt()

                        if (wavFile.numChannels == 0)
                            throw Exception("Number of channels specified in header is equal to zero")

                        if (wavFile.blockAlign == 0)
                            throw Exception("Block Align specified in header is equal to zero")

                        if (wavFile.validBits < 2)
                            throw Exception("Valid Bits specified in header is less than 2")

                        if (wavFile.validBits > 64)
                            throw Exception("Valid Bits specified in header is greater than 64, this is greater than a long can hold")

                        // Calculate the number of bytes required to hold 1 sample
                        wavFile.bytesPerSample = (wavFile.validBits + 7) / 8

                        if (wavFile.bytesPerSample * wavFile.numChannels != wavFile.blockAlign) throw Exception(
                            "Block Align does not agree with bytes required for validBits and number of channels"
                        )

                        // Account for number of format bytes and then skip over
                        // any extra format bytes
                        numChunkBytes -= 16
                        if (numChunkBytes > 0) wavFile.inStream!!.skip(numChunkBytes)
                    }

                    DATA_CHUNK_ID.toLong() -> {
                        // Check if we've found the format chunk,
                        // If not, throw an exception as we need the format information
                        // before we can read the data chunk
                        if (!isFormatFound)
                            throw Exception("Data chunk found before Format chunk")

                        // Check that the chunkSize (wav data length) is a multiple of the
                        // block align (bytes per frame)
                        if (chunkSize % wavFile.blockAlign != 0L)
                            throw Exception("Data Chunk size is not multiple of Block Align")

                        // Calculate the number of frames
                        wavFile.numFrames = chunkSize / wavFile.blockAlign

                        // Flag that we've found the wave data chunk
                        isDataFound = true
                        break
                    }

                    else -> {
                        // If an unknown chunk ID is found, just skip over the chunk data
                        wavFile.inStream!!.skip(numChunkBytes)
                    }
                }
            }

            // Throw an exception if no data chunk has been found
            if (!isDataFound)
                throw Exception("Did not find a data chunk")

            // Calculate the scaling factor for converting to a normalised double
            when {
                wavFile.validBits > 8 -> {
                    // If more than 8 validBits, data is signed
                    // Conversion required dividing by magnitude of max negative value
                    wavFile.floatOffset = 0.0
                    wavFile.floatScale = (1 shl wavFile.validBits - 1).toDouble()
                }

                else -> {
                    // Else if 8 or less validBits, data is unsigned
                    // Conversion required dividing by max positive value
                    wavFile.floatOffset = -1.0
                    wavFile.floatScale = 0.5 * ((1 shl wavFile.validBits) - 1)
                }
            }

            wavFile.bufferPointer = 0
            wavFile.bytesReadNum = 0
            wavFile.frameCounter = 0
            wavFile.ioState = IOState.READING
            return wavFile
        }

        fun openWavFileCatching(file: File) = kotlin.runCatching { openWavFile(file) }

        /** Get and Put little endian data from local buffer */

        private fun getLE(buffer: ByteArray, pos: Int, numBytes: Int): Long {
            var posMut = pos + numBytes - 1
            val numBytesMut = numBytes - 1

            val sample = (0 until numBytesMut)
                .fold((buffer[posMut].toInt() and 0xFF).toLong()) { sample, _ ->
                    (sample shl 8) + (buffer[--posMut].toInt() and 0xFF)
                }

            return sample
        }

        private fun putLE(sample: Sample, buffer: ByteArray, pos: Int, numBytes: Int) {
            var sampleMut = sample
            var posMut = pos

            repeat(numBytes) {
                buffer[posMut] = (sampleMut and 0xFFL).toByte()
                sampleMut = sampleMut shr 8
                ++posMut
            }
        }
    }

    private enum class IOState { READING, WRITING, CLOSED }

    /** File that will be read from or written to */
    var file: File? = null
        private set

    /** Specifies the IO State of the Wav File (used for sanity checking) */
    private var ioState: IOState? = null

    /** Number of bytes required to store a single sample */
    private var bytesPerSample = 0

    /** Number of frames within the data section */
    var numFrames: Long = 0
        private set

    /** Output stream used for writing data */
    private var outStream: FileOutputStream? = null

    /** Input stream used for reading data */
    private var inStream: FileInputStream? = null

    /** Scaling factor used for int <-> float conversion */
    private var floatScale = 0.0

    /** Offset factor used for int <-> float conversion */
    private var floatOffset = 0.0

    /**
     * Specify if an extra byte at the end
     * of the data chunk is required for word alignment
     */
    private var isWordAlignAdjust = false

    // ------------------------ Header ------------------------

    var numChannels = 0
        private set

    var sampleRate = 0L
        private set

    private var blockAlign = 0

    var validBits = 0
        private set

    // ------------------------ Buffering ------------------------

    /** Local buffer used for IO */
    private val buffer = ByteArray(BUFFER_SIZE)

    /** Points to the current position in local buffer */
    private var bufferPointer = 0

    /** Bytes read after last read into local buffer */
    private var bytesReadNum = 0

    /** Current number of frames read or written */
    private var frameCounter = 0L

    internal inline val framesRemaining: Long
        get() = numFrames - frameCounter

    /** Sample Writing and Reading */

    private fun writeSample(sample: Sample) {
        var sampleMut = sample

        repeat(bytesPerSample) { byte ->
            if (bufferPointer == BUFFER_SIZE) {
                outStream!!.write(buffer, 0, BUFFER_SIZE)
                bufferPointer = 0
            }

            buffer[bufferPointer] = (sampleMut and 0xFFL).toByte()
            sampleMut = sampleMut shr 8
            bufferPointer++
        }
    }

    private fun readSample(): Sample {
        var sample = 0L

        repeat(bytesPerSample) { byteInd ->
            if (bufferPointer == bytesReadNum) {
                val readNum = inStream!!
                    .read(buffer, 0, BUFFER_SIZE)
                    .takeIf { it != -1 }
                    ?: throw Exception("Not enough data available")

                bytesReadNum = readNum
                bufferPointer = 0
            }

            val byte = buffer[bufferPointer].toInt().let { byte ->
                byte
                    .takeIf { byteInd >= bytesPerSample - 1 && bytesPerSample > 1 }
                    ?: (byte and 0xFF)
            }

            sample += (byte shl byteInd * 8).toLong()
            bufferPointer++
        }

        return sample
    }

    private fun readSampleCatching() = kotlin.runCatching { readSample() }

    fun readFrames(sampleBuffer: IntArray, numFramesToRead: Int, offset: Int = 0): Int {
        var offsetMut = offset

        if (ioState != IOState.READING)
            throw IOException("Cannot read from WavFile instance")

        repeat(numFramesToRead) { frames ->
            if (frameCounter == numFrames)
                return frames

            repeat(numChannels) { sampleBuffer[offsetMut++] = readSample().toInt() }
            ++frameCounter
        }

        return numFramesToRead
    }

    fun readFramesCatching(
        sampleBuffer: IntArray,
        numFramesToRead: Int,
        offset: Int = 0,
    ) = kotlin.runCatching { readFrames(sampleBuffer, offset, numFramesToRead) }

    fun readFrames(sampleBuffer: Array<IntArray>, numFramesToRead: Int, offset: Int = 0): Int {
        var offsetMut = offset

        if (ioState != IOState.READING)
            throw IOException("Cannot read from WavFile instance")

        repeat(numFramesToRead) { frames ->
            if (frameCounter == numFrames)
                return frames

            repeat(numChannels) { channel ->
                sampleBuffer[channel][offsetMut] = readSample().toInt()
            }

            ++offsetMut
            ++frameCounter
        }

        return numFramesToRead
    }

    fun readFramesCatching(
        sampleBuffer: Array<IntArray>,
        numFramesToRead: Int,
        offset: Int = 0,
    ) = kotlin.runCatching { readFrames(sampleBuffer, offset, numFramesToRead) }

    fun writeFrames(sampleBuffer: IntArray, numFramesToWrite: Int, offset: Int = 0): Int {
        var offsetMut = offset

        if (ioState != IOState.WRITING)
            throw IOException("Cannot write to WavFile instance")

        repeat(numFramesToWrite) { frames ->
            if (frameCounter == numFrames)
                return frames

            repeat(numChannels) { writeSample(sampleBuffer[offsetMut++].toLong()) }
            ++frameCounter
        }

        return numFramesToWrite
    }

    fun writeFramesCatching(
        sampleBuffer: IntArray,
        numFramesToWrite: Int,
        offset: Int = 0
    ) = kotlin.runCatching { writeFrames(sampleBuffer, numFramesToWrite, offset) }

    fun writeFrames(sampleBuffer: Array<IntArray>, numFramesToWrite: Int, offset: Int = 0): Int {
        var offsetMut = offset

        if (ioState != IOState.WRITING)
            throw IOException("Cannot write to WavFile instance")

        repeat(numFramesToWrite) { frames ->
            if (frameCounter == numFrames)
                return frames

            repeat(numChannels) { channel ->
                writeSample(sampleBuffer[channel][offsetMut].toLong())
            }

            ++offsetMut
            ++frameCounter
        }

        return numFramesToWrite
    }

    fun writeFramesCatching(
        sampleBuffer: Array<IntArray>,
        numFramesToWrite: Int,
        offset: Int = 0
    ) = kotlin.runCatching { writeFrames(sampleBuffer, numFramesToWrite, offset) }

    fun readFrames(sampleBuffer: LongArray, numFramesToRead: Int, offset: Int = 0): Int {
        var offsetMut = offset

        if (ioState != IOState.READING)
            throw IOException("Cannot read from WavFile instance")

        repeat(numFramesToRead) { frames ->
            if (frameCounter == numFrames)
                return frames

            repeat(numChannels) { sampleBuffer[offsetMut++] = readSample() }
            ++frameCounter
        }

        return numFramesToRead
    }

    fun readFramesCatching(
        sampleBuffer: LongArray,
        numFramesToRead: Int,
        offset: Int = 0
    ) = kotlin.runCatching { readFrames(sampleBuffer, numFramesToRead, offset) }

    fun readFrames(sampleBuffer: Array<LongArray>, numFramesToRead: Int, offset: Int = 0): Int {
        var offsetMut = offset

        if (ioState != IOState.READING)
            throw IOException("Cannot read from WavFile instance")

        repeat(numFramesToRead) { frames ->
            if (frameCounter == numFrames)
                return frames

            repeat(numChannels) { channel ->
                sampleBuffer[channel][offsetMut] = readSample()
            }

            ++offsetMut
            ++frameCounter
        }

        return numFramesToRead
    }

    fun readFramesCatching(
        sampleBuffer: Array<LongArray>,
        numFramesToRead: Int,
        offset: Int = 0
    ) = kotlin.runCatching { readFrames(sampleBuffer, numFramesToRead, offset) }

    fun writeFrames(sampleBuffer: LongArray, numFramesToWrite: Int, offset: Int = 0): Int {
        var offsetMut = offset

        if (ioState != IOState.WRITING)
            throw IOException("Cannot write to WavFile instance")

        repeat(numFramesToWrite) { frames ->
            if (frameCounter == numFrames)
                return frames

            repeat(numChannels) { writeSample(sampleBuffer[offsetMut++]) }
            ++frameCounter
        }

        return numFramesToWrite
    }

    fun writeFramesCatching(
        sampleBuffer: LongArray,
        numFramesToWrite: Int,
        offset: Int = 0
    ) = kotlin.runCatching { writeFrames(sampleBuffer, numFramesToWrite, offset) }

    fun writeFrames(sampleBuffer: Array<LongArray>, numFramesToWrite: Int, offset: Int = 0): Int {
        var offsetMut = offset

        if (ioState != IOState.WRITING)
            throw IOException("Cannot write to WavFile instance")

        repeat(numFramesToWrite) { frames ->
            if (frameCounter == numFrames)
                return frames

            repeat(numChannels) { channel ->
                writeSample(sampleBuffer[channel][offsetMut])
            }

            ++offsetMut
            ++frameCounter
        }

        return numFramesToWrite
    }

    fun writeFramesCatching(
        sampleBuffer: Array<LongArray>,
        numFramesToWrite: Int,
        offset: Int = 0
    ) = kotlin.runCatching { writeFrames(sampleBuffer, numFramesToWrite, offset) }

    fun readFrames(sampleBuffer: DoubleArray, numFramesToRead: Int, offset: Int = 0): Int {
        var offsetMut = offset

        if (ioState != IOState.READING)
            throw IOException("Cannot read from WavFile instance")

        repeat(numFramesToRead) { frames ->
            if (frameCounter == numFrames)
                return frames

            repeat(numChannels) {
                sampleBuffer[offsetMut++] = floatOffset + readSample().toDouble() / floatScale
            }

            ++frameCounter
        }

        return numFramesToRead
    }

    fun readFramesCatching(
        sampleBuffer: DoubleArray,
        numFramesToRead: Int,
        offset: Int = 0
    ) = kotlin.runCatching { readFrames(sampleBuffer, numFramesToRead, offset) }

    fun readFrames(sampleBuffer: Array<DoubleArray>, numFramesToRead: Int, offset: Int = 0): Int {
        var offsetMut = offset

        if (ioState != IOState.READING)
            throw IOException("Cannot read from WavFile instance")

        repeat(numFramesToRead) { frames ->
            if (frameCounter == numFrames)
                return frames

            repeat(numChannels) { channel ->
                sampleBuffer[channel][offsetMut] =
                    floatOffset + readSample().toDouble() / floatScale
            }

            ++offsetMut
            ++frameCounter
        }

        return numFramesToRead
    }

    fun readFramesCatching(
        sampleBuffer: Array<DoubleArray>,
        numFramesToRead: Int,
        offset: Int = 0
    ) = kotlin.runCatching { readFrames(sampleBuffer, numFramesToRead, offset) }

    fun writeFrames(sampleBuffer: DoubleArray, numFramesToWrite: Int, offset: Int = 0): Int {
        var offsetMut = offset

        if (ioState != IOState.WRITING)
            throw IOException("Cannot write to WavFile instance")

        repeat(numFramesToWrite) { frames ->
            if (frameCounter == numFrames)
                return frames

            repeat(numChannels) {
                writeSample((floatScale * (floatOffset + sampleBuffer[offsetMut++])).toLong())
            }

            ++frameCounter
        }

        return numFramesToWrite
    }

    fun writeFramesCatching(
        sampleBuffer: DoubleArray,
        numFramesToWrite: Int,
        offset: Int = 0
    ) = kotlin.runCatching { writeFrames(sampleBuffer, numFramesToWrite, offset) }

    fun writeFrames(sampleBuffer: Array<DoubleArray>, numFramesToWrite: Int, offset: Int = 0): Int {
        var offsetMut = offset

        if (ioState != IOState.WRITING)
            throw IOException("Cannot write to WavFile instance")

        repeat(numFramesToWrite) { frames ->
            if (frameCounter == numFrames)
                return frames

            repeat(numChannels) { channel ->
                writeSample(
                    (floatScale * (floatOffset + sampleBuffer[channel][offsetMut])).toLong()
                )
            }

            ++offsetMut
            ++frameCounter
        }

        return numFramesToWrite
    }

    override fun close() {
        if (inStream != null) {
            inStream!!.close()
            inStream = null
        }

        if (outStream != null) {
            // Write out anything still in the local buffer
            if (bufferPointer > 0) outStream!!.write(buffer, 0, bufferPointer)

            // If an extra byte is required for word alignment, add it to the end
            if (isWordAlignAdjust) outStream!!.write(0)

            // Close the stream and set to null
            outStream!!.close()
            outStream = null
        }

        // Flag that the stream is closed
        ioState = IOState.CLOSED
    }

    override fun toString() =
        "WavFile{file=$file, numChannels=$numChannels, numFrames=$numFrames, ioState=$ioState, sampleRate=$sampleRate, blockAlign=$blockAlign, validBits=$validBits, bytesPerSample=$bytesPerSample}"
}