package com.pixelatedmind.game.tinyuniverse.generation.music.synth.filter

import kotlin.math.pow
import kotlin.math.sqrt

class EqualizerFilter(private val bands: FloatArray, private val gains: FloatArray) {
    private val numBands = bands.size

    fun apply(audioData: FloatArray) {
        for (i in 0 until numBands) {
            val band = bands[i]
            val gain = gains[i]
            applyBand(audioData, band, gain)
        }
    }

    private fun applyBand(audioData: FloatArray, band: Float, gain: Float) {
        val sampleRate = 44100 // Use your own sample rate here
        val nyquist = sampleRate / 2
        val centerFreq = band
        val bandwidth = 1.0f // Use your own bandwidth value here
        val w0 = 2.0f * Math.PI * centerFreq / sampleRate
        val alpha = Math.sin(w0) * sinh((ln2 / 2 * bandwidth * w0 / Math.sin(w0)).toFloat())
        val a0 = 1.0f + alpha / gain
        val a1 = -2.0f * Math.cos(w0) / a0
        val a2 = (1.0f - alpha / gain) / a0
        val b0 = (1.0f + gain * alpha) / a0
        val b1 = -2.0f * Math.cos(w0) / a0
        val b2 = (1.0f - gain * alpha) / a0
        val x1 = floatArrayOf(0f, 0f, 0f)
        val x2 = floatArrayOf(0f, 0f, 0f)
        val y1 = floatArrayOf(0f, 0f, 0f)
        val y2 = floatArrayOf(0f, 0f, 0f)
        for (i in audioData.indices) {
            val x0 = audioData[i]
            val y0 =
                    b0 * x0 + b1 * x1[0] + b2 * x2[0] - a1 * y1[0] - a2 * y2[0]
            x2[0] = x1[0]
            x1[0] = x0
            y2[0] = y1[0]
            y1[0] = y0.toFloat()
            audioData[i] = y0.toFloat()
        }
    }

    private fun sinh(x: Float): Float {
        return (Math.exp(x.toDouble()) - Math.exp(-x.toDouble())).toFloat() / 2.0f
    }

    companion object{
        private const val ln2 = 0.6931471805599453f
        fun emphasizeMidRange() : EqualizerFilter{
            val frequencies = floatArrayOf(60f, 170f, 310f, 600f, 1000f, 3000f, 6000f, 10000f)
            val gains = floatArrayOf(.1f, 1f, 2f, 2f, 2f, 1f, .5f, .1f)

//            for (i in frequencies.indices) {
//                val freq = frequencies[i]
//
//                // Emphasize midrange frequencies
//                if (freq >= 200f && freq <= 1000f) {
//                    gains[i] = 2f
//                } else {
//                    gains[i] = 1f
//                }
//            }
            val filter = EqualizerFilter(frequencies, gains)
            return filter
        }
    }
}