package com.pixelatedmind.game.tinyuniverse.generation.music.waveform

import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream

class AdditiveWaveform(var waveForms : List<FloatInputStream> = listOf(), var weights : List<Float> = listOf()) : FloatInputStream {
    var toggle : Boolean = false
    override fun read(timeInSeconds: Float): Float {
        toggle = !toggle
        if(!waveForms.any()){
            if(toggle) return .01f
            else return -.01f
        }
        var result = 0f
        waveForms.forEachIndexed{index, stream ->
            val weight = weights[index]
            val weightedValue = (stream.read(timeInSeconds) * weight)
            result += weightedValue
        }
        return result
    }
}