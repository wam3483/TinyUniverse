package com.pixelatedmind.game.tinyuniverse.generation.music.synth.effect

import com.pixelatedmind.game.tinyuniverse.generation.music.synth.stream.FloatInputStream

class GainEffect(var waveform : FloatInputStream, var amplitudeStream : FloatInputStream) : FloatInputStream {

    override fun read(timeInSeconds: Float): Float {
        var value = waveform.read(timeInSeconds)
        val amp = amplitudeStream.read(timeInSeconds)
        return value * amp
    }
}