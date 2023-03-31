package com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.ui.PiecewiseModel
import kotlin.math.sin

class SineWaveform(val frequency:FloatInputStream, var samplingRate : Int) : FloatInputStream {
    private val onePeriod = (Math.PI*2).toFloat()
    private var phase = 0f
    override fun read(timeInSeconds:Float) : Float {
        val frequency = frequency.read(timeInSeconds)
        val currentPhase = onePeriod * frequency / samplingRate
        phase += currentPhase

        val result = sin(phase)
        return result
    }
}