package com.pixelatedmind.game.tinyuniverse.generation.music.synth.filter

import com.pixelatedmind.game.tinyuniverse.generation.music.filter.BiQuadFilter
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream

class BiQuadFilter(val stream : FloatInputStream, val sampleRate : Float, cutoffFreq:Float, resonance:Float) : FloatInputStream {
    val lowpass : BiQuadFilter
    init{
        lowpass = BiQuadFilter.LowPassFilter(sampleRate,cutoffFreq,resonance)
    }
    override fun read(timeInSeconds: Float): Float {
        val sample = stream.read(timeInSeconds)
        val lowpassOutput = lowpass.Transform(sample)
        val output = lowpassOutput
        return output
    }
}