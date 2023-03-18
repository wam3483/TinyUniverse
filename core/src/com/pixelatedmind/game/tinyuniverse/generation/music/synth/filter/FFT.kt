package com.pixelatedmind.game.tinyuniverse.generation.music.synth.filter

import java.lang.Math.*

class FFT(private val size: Int) {
    private val cosTable: FloatArray
    private val sinTable: FloatArray

    init {
        require(size > 0 && size and (size - 1) == 0) { "FFT size must be a power of 2" }

        cosTable = FloatArray(size / 2)
        sinTable = FloatArray(size / 2)

        for (i in 0 until size / 2) {
            cosTable[i] = cos(-2 * PI * i / size).toFloat()
            sinTable[i] = sin(-2 * PI * i / size).toFloat()
        }
    }

    fun forward(data: FloatArray) {
        val spectrum = FloatArray(size / 2)

        // Bit-reverse the input data
        bitReverse(data)

        // Perform the FFT
        for (step in 2..size step 2) {
            for (i in 0 until size step step) {
                for (j in i until i + step / 2) {
                    val k = (j - i) * size / step
                    val t = cosTable[k] * data[j + step / 2] - sinTable[k] * data[j + step / 2 + 1]
                    data[j + step / 2 + 1] = sinTable[k] * data[j + step / 2] + cosTable[k] * data[j + step / 2 + 1]
                    data[j + step / 2] = t.toFloat()
                }
            }
        }

        // Compute the spectrum
        for (i in 0 until size / 2) {
            val re = data[2 * i]
            val im = data[2 * i + 1]
            spectrum[i] = sqrt((re * re + im * im).toDouble()).toFloat()
        }

        // Copy the spectrum to the input array
        for (i in data.indices) {
            if (i < size / 2) {
                data[i] = spectrum[i]
            } else {
                data[i] = 0f
            }
        }
    }

    fun inverse(data: FloatArray) {
        // Conjugate the input data
        for (i in 1 until size / 2) {
            data[2 * size - 2 * i + 1] = -data[2 * i + 1]
        }

        // Perform the inverse FFT
        forward(data)

        // Scale the output
        for (i in data.indices) {
            data[i] = data[i] / size
        }
    }

    private fun bitReverse(data: FloatArray) {
        var j = 0
        for (i in 0 until size) {
            if (i < j) {
                data.swap(i, j)
            }
            var k = size / 2
            while (k <= j) {
                j -= k
                k /= 2
            }
            j += k
        }
    }

    private fun FloatArray.swap(i: Int, j: Int) {
        val temp = this[i]
        this[i] = this[j]
        this[j] = temp
    }
}