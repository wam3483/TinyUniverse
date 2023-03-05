package com.pixelatedmind.game.tinyuniverse.generation.music.waveform

import com.pixelatedmind.game.tinyuniverse.generation.music.values.AnimatedValue

class SawtoothWaveform(frequency : AnimatedValue, amplitude : Float=1f, phaseShift : Float = 0f) : AbstractShapeWaveform(frequency, amplitude, phaseShift) {

    override fun read(timeInSeconds: Float): Float {
        val freq = frequency.getValue(timeInSeconds)
        val period = 1 / freq
        val result = (timeInSeconds % period) / period * 2 - 1
        return result
    }
}