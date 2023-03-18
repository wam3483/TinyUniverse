package com.pixelatedmind.game.tinyuniverse.generation.music.filter

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform.SineWaveform

class SubBassInputStream(normalScale : Float, frequency : FloatInputStream) : FloatInputStream {
    private var maxWaveAmplitude : Float = 0f
    private val sineWave = SineWaveform(frequency)
    init{
        maxWaveAmplitude = normalScale
    }
    override fun read(timeInSeconds: Float): Float {
        val output = sineWave.read(timeInSeconds)
        return output * maxWaveAmplitude
    }
}