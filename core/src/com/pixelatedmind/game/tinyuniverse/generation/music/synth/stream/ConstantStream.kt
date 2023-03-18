package com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream

class ConstantStream(var value : Float) : FloatInputStream {
    override fun read(timeInSeconds: Float): Float {
        return value
    }
}