package com.pixelatedmind.game.tinyuniverse.generation.music

import com.badlogic.gdx.math.Interpolation


class WaveformInterpolator(val waveform1 : FloatInputStream, val waveform2 : FloatInputStream, var alpha : Float, var interpolator : Interpolation) : FloatInputStream{
    override fun read(timeInSeconds: Float): Float {
        val v1 = waveform1.read(timeInSeconds)
        val v2 = waveform2.read(timeInSeconds)
        return interpolator.apply(v1, v2, alpha)
    }

}