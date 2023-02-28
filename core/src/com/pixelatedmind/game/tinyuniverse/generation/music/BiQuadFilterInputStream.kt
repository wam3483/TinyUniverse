package com.pixelatedmind.game.tinyuniverse.generation.music

import com.pixelatedmind.game.tinyuniverse.generation.music.filter.BiQuadFilter

class BiQuadFilterInputStream(val stream : FloatInputStream, val sampleRate : Float, cutoffFreq:Float, resonance:Float) : FloatInputStream {
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