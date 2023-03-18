package com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream
import kotlin.math.sin

class SineWaveform(val frequency:FloatInputStream) : FloatInputStream {
    private val onePeriod = Math.PI*2

    override fun read(timeInSeconds:Float) : Float {
        val freq = frequency.read(timeInSeconds)
        val result = sin(timeInSeconds * onePeriod * freq).toFloat()
        return result
    }
}