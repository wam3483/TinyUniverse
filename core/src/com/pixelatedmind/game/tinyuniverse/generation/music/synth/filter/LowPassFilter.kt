package com.pixelatedmind.game.tinyuniverse.generation.music.synth.filter

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream
import kotlin.math.*

class LowPassFilter(private var sampleRate: Double,
                    private var cutoffFreqStream: FloatInputStream,
                    private var resonanceStream: FloatInputStream,
                    private var overdriveStream: FloatInputStream,
                    private var slope: Int,
                    private val inputStream : FloatInputStream) : FloatInputStream{
    val filter : LowPassFilterRaw
    init{
        filter = LowPassFilterRaw(sampleRate,0.0,0.0,0.0,slope)
    }
    override fun read(timeInSeconds: Float): Float {
        val resonanceValue = resonanceStream.read(timeInSeconds).toDouble()
        val cutoffFreqValue = cutoffFreqStream.read(timeInSeconds).toDouble()
        val overdriveValue = overdriveStream.read(timeInSeconds).toDouble()
        filter.setParameters(cutoffFreqValue,resonanceValue, overdriveValue, slope)

        val input = inputStream.read(timeInSeconds).toDouble()
        val output = filter.process(input).toFloat()
        return output
    }
}

class LowPassFilterRaw(private var sampleRate: Double,
                       private var cutoffFreq: Double,
                       private var resonance: Double,
                       private var overdrive: Double,
                       private var slope: Int) {
    private var buf0 = 0.0
    private var buf1 = 0.0
    private var buf2 = 0.0
    private var buf3 = 0.0
    private var buf4 = 0.0

    private var f = 0.0
    private var q = 0.0
    private var damp = 0.0

    init {
        setParameters(cutoffFreq, resonance, overdrive, slope)
    }

    fun setParameters(frequency: Double, res: Double, over: Double, s: Int) {
        resonance = res
        overdrive = over
        slope = s
        cutoffFreq = frequency

        val fc = frequency / sampleRate
//        q = 1.0 - fc.pow((0.7 + (res * 0.6)))
        val qBase = when {
            res < 0.5 -> 1.0 - fc.pow((0.7 + (res * 0.6))) // For lower resonance values
            else -> fc.pow((1.0 - res) * 5.0) // For higher resonance values
        }
        q = qBase.coerceIn(0.1, 0.99) // Ensure q is within valid range
        damp = min(2.0 - q, 0.99) // Corrected damping factor calculation

        when (s) {
            6 -> {
                val g = Math.pow(0.5, damp * 0.5)
                f = (1.0 - g) / (1.0 + g)
//                f = (1.0 - fc.pow(2.0)) * damp
            }
            12 -> {
                val g = Math.pow(0.5, damp * 0.25)
                f = (1.0 - g) / (1.0 + g)
                //f = (1.0 - fc) * damp
            }
            18 -> {
                val g = Math.pow(0.5, damp * 0.16609640474436813) // 1.0 / sqrt(2.0) = 0.7071067811865485
                f = (1.0 - g) / (1.0 + g)
                //f = (sqrt(1.0 - fc)) * damp
            }
            24 -> {
//                val g = Math.pow(0.5, damp * 0.125)
//                f = (1.0 - g) / (1.0 + g)
                f =1.0 - exp(-2.0 * PI * fc * damp)
            }
            else -> throw IllegalArgumentException("Invalid slope")
        }
    }

    fun process(input: Double): Double {
        val inputWithOverdrive = input * overdrive
        buf0 += f * (inputWithOverdrive - buf0 + resonance * (buf0 - buf1))
        buf1 += f * (buf0 - buf1)
        buf2 += f * (buf1 - buf2)
        buf3 += f * (buf2 - buf3)
        buf4 += f * (buf3 - buf4)

        return buf4
    }
}