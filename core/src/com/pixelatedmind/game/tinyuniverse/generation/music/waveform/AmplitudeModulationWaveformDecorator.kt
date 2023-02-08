package com.pixelatedmind.game.tinyuniverse.generation.music.waveform

import com.pixelatedmind.game.tinyuniverse.generation.music.FloatInputStream

class AmplitudeModulationWaveformDecorator(val waveform : FloatInputStream, var amplitude : Float) : FloatInputStream{
    override fun read(timeInSeconds: Float): Float {
        val value = waveform.read(timeInSeconds) * amplitude
        return value
    }
}