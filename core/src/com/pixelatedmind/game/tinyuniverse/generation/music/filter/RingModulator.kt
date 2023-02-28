package com.pixelatedmind.game.tinyuniverse.generation.music.filter

import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream

class RingModulator(val inputStream : FloatInputStream, val modulatingStream : FloatInputStream) : FloatInputStream {
    override fun read(timeInSeconds: Float): Float {
        val modulationValue = Math.abs(modulatingStream.read(timeInSeconds))
        val inputValue = inputStream.read(timeInSeconds)
        return inputValue * modulationValue
    }
}