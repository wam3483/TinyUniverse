package com.pixelatedmind.game.tinyuniverse.generation.music

class SilentInputStream(val amp:Float = .01f) : FloatInputStream {
    private var toggle = false
    override fun read(timeInSeconds: Float): Float {
        toggle = !toggle
        return when(toggle){
            true -> amp
            false -> -amp
        }
    }
}