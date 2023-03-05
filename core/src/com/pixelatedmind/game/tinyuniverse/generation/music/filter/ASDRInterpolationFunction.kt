package com.pixelatedmind.game.tinyuniverse.generation.music.filter

import com.badlogic.gdx.math.Interpolation

class ASDRInterpolationFunction(private val durationSecs : Float, private val targetAmplitude : Float = 1f, private val interp: Interpolation) : PiecewiseTimeFunction{

    override fun read(startingValue: Float, timeInSeconds: Float): Float {
        if(timeInSeconds < durationSecs) {
            val alpha = timeInSeconds / durationSecs
            return interp.apply(startingValue, targetAmplitude, alpha)
        }
        else {
            return targetAmplitude
        }
    }
}