package com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream

class MultiplexGainEffect(var waveForms : List<FloatInputStream> = listOf(), var weights : List<Float> = listOf()) : FloatInputStream {
    var toggle : Boolean = false
    override fun read(timeInSeconds: Float): Float {
        toggle = !toggle
        if(!waveForms.any()){
            if(toggle) return .01f
            else return -.01f
        }
        var result = 0f
        waveForms.forEachIndexed{index, stream ->
            var weightedValue =
                if(weights.any()){
                    val weight = weights[index]
                    stream.read(timeInSeconds) * weight
                }else{
                    stream.read(timeInSeconds)
                }
            result += weightedValue
        }
        return 1f.coerceAtMost((-1f).coerceAtLeast(result))
    }
}