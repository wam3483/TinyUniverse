package com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream

class StreamClipEffect(val stream : FloatInputStream, val min :Float = 0f, val max : Float = 1f) : FloatInputStream {
    override fun read(timeElapsed: Float): Float {
        var value = stream.read(timeElapsed)
        return Math.min(max, Math.max(min, value))
    }
}