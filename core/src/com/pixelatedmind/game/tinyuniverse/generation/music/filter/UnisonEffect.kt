package com.pixelatedmind.game.tinyuniverse.generation.music.filter

import com.pixelatedmind.game.tinyuniverse.generation.music.EnvelopeFactory
import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream

class UnisonEffect(val streamFactory : EnvelopeFactory,
                   val duplications : Int,
                   val detunePercent:Float) : FloatInputStream {

    override fun read(timeInSeconds: Float): Float {
        TODO("Not yet implemented")
    }
}