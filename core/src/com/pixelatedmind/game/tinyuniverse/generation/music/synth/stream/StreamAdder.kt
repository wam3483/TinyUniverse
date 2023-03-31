package com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream

import com.pixelatedmind.game.tinyuniverse.ui.PiecewiseModel

class StreamAdder(val streams : List<FloatInputStream>) : FloatInputStream {

    constructor(vararg streams : FloatInputStream) : this(streams.toList())

    override fun read(timeInSeconds: Float): Float {
        var result = 0f
        streams.forEach{
            result += it.read(timeInSeconds)
        }
        return result
    }
}