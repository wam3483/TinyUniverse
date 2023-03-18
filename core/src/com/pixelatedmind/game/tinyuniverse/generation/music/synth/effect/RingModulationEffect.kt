package com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream

class RingModulationEffect(val inputStream : FloatInputStream, val modulatingStream : FloatInputStream) : FloatInputStream {
    override fun read(timeInSeconds: Float): Float {
        val modulationValue = Math.abs(modulatingStream.read(timeInSeconds))
        val inputValue = inputStream.read(timeInSeconds)
        return inputValue * modulationValue
    }
}