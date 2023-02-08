package com.pixelatedmind.game.tinyuniverse.generation.music.waveform

import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream

class AdditiveWaveform(var waveForms : List<FloatInputStream> = listOf()) : FloatInputStream {
    var toggle : Boolean = false
    override fun read(timeInSeconds: Float): Float {
        toggle = !toggle
        if(!waveForms.any()){
            if(toggle) return .01f
            else return -.01f
        }
        val result = waveForms.map{it.read(timeInSeconds)}.sum() / waveForms.size
        return result
    }
}