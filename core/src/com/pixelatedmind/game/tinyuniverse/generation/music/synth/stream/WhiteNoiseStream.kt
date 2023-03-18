package com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream

import java.util.*

class WhiteNoiseStream(val random : Random) : FloatInputStream {
    override fun read(timeInSeconds: Float): Float {
        return random.nextFloat()*2-1
    }
}