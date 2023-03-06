package com.pixelatedmind.game.tinyuniverse.generation.music.noise

import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream
import java.util.*

class WhiteNoise(val random : Random) : FloatInputStream {
    override fun read(timeInSeconds: Float): Float {
        return random.nextFloat()*2-1
    }
}