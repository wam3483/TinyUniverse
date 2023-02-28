package com.pixelatedmind.game.tinyuniverse.generation.music.values

import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream

class NormalizedValueStream(val stream : FloatInputStream, val min :Float = 0f, val max : Float = 1f) : NormalizedValue {
    override fun getValue(timeElapsed: Float): Float {
        var value = stream.read(timeElapsed)
        value = (value+1) /2
        return Math.min(max, Math.max(min, value))
    }
}