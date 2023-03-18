package com.pixelatedmind.game.tinyuniverse.generation.music.synth.filter

import com.pixelatedmind.game.tinyuniverse.generation.music.filter.PassType
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.ConstantStream
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream

class HighLowPassFilterInputStream2(
        val cutoffFrequency : FloatInputStream,
        val resonance: FloatInputStream = ConstantStream(1f),
        sampleRate:Int,
        passType: PassType,
        var stream : FloatInputStream) : FloatInputStream {

    val filter = LowHighPassFilter(sampleRate.toDouble(), 1.0, 1.0, passType == PassType.Low)

    fun setPassType(passType : PassType){
        this.filter.lowPass = passType == PassType.Low
    }
    override fun read(timeInSeconds: Float): Float {
        val cutoff = cutoffFrequency.read(timeInSeconds)
        val res = resonance.read(timeInSeconds)
        filter.cutoffFrequency = cutoff.toDouble()
        filter.resonance = res.toDouble()

        val sample = stream.read(timeInSeconds)
        val result = filter.filter(sample)

        return result
    }
}