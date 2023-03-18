package com.pixelatedmind.game.tinyuniverse.generation.music.synth.filter

import com.pixelatedmind.game.tinyuniverse.generation.music.filter.HighLowPassFilter
import com.pixelatedmind.game.tinyuniverse.generation.music.filter.PassType
import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream

class HighLowPassFilterInputStream(cutoffFrequency : Float, resonance: Float = 1f, sampleRate:Int, passType: PassType, var stream : FloatInputStream) : FloatInputStream {
    val highLowPassFilter : HighLowPassFilter
    init{
        highLowPassFilter = HighLowPassFilter(cutoffFrequency,sampleRate,passType, resonance)
    }

    fun setFrequency(frequency:Float){
        highLowPassFilter.setFrequency(frequency)
    }

    fun setResonance(resonance:Float){
        highLowPassFilter.setResonance(resonance)
    }

    fun setSampleRate(sampleRate:Int){
        highLowPassFilter.setSampleRate(sampleRate)
    }

    fun setPassType(passType: PassType){
        highLowPassFilter.setPassType(passType)
    }
    override fun read(timeInSeconds: Float): Float {
        val value = stream.read(timeInSeconds)
        val filteredValue = highLowPassFilter.filter(value)
        return filteredValue
    }

}