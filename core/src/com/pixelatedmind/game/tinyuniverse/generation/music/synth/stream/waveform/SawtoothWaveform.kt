package com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.waveform

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream

class SawtoothWaveform(val frequency : FloatInputStream) : FloatInputStream {

    override fun read(timeInSeconds: Float): Float {
        val freq = frequency.read(timeInSeconds)
        val period = 1 / freq
        val result = (timeInSeconds % period) / period * 2 - 1
        return result
    }
}