package com.pixelatedmind.game.tinyuniverse.generation.music

import com.pixelatedmind.game.tinyuniverse.generation.music.filter.HighLowPassFilter

class HighLowPassFilterInputStream(val filter : HighLowPassFilter, val stream : FloatInputStream) : FloatInputStream {
    override fun read(timeInSeconds: Float): Float {
        val value = stream.read(timeInSeconds)
        val filteredValue = filter.filter(value)
        return filteredValue
    }

}