package com.pixelatedmind.game.tinyuniverse.generation.music.waveform

import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream

class SawtoothWaveform(frequency : Float, amplitude : Float=1f, phaseShift : Float = 0f) : AbstractShapeWaveform(frequency, amplitude, phaseShift) {

    override fun read(timeInSeconds: Float): Float {
        return (((timeInSeconds % period)+periodShift) / period * 2 - 1) * amplitude
    }
}