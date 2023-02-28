package com.pixelatedmind.game.tinyuniverse.generation.music.filter

import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream
import com.pixelatedmind.game.tinyuniverse.generation.music.values.ConstantValue
import com.pixelatedmind.game.tinyuniverse.generation.music.values.NormalizedValue
import com.pixelatedmind.game.tinyuniverse.generation.music.waveform.SineWaveform
import kotlin.math.pow

class SubBassInputStream(normalScale : Float, frequency : NormalizedValue) : FloatInputStream {
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