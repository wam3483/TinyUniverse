package com.pixelatedmind.game.tinyuniverse.generation.music.filter

import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream

class AdditiveModulator(val streams : List<FloatInputStream>) : FloatInputStream {
    override fun read(timeInSeconds: Float): Float {
        var sum = 0f
        streams.forEach{
            sum += it.read(timeInSeconds)
        }
        return Math.min(1f, Math.max(-1f, sum))
    }
}