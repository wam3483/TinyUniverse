package com.pixelatedmind.game.tinyuniverse.generation.math

import com.pixelatedmind.game.tinyuniverse.generation.math.PiecewiseTimeFunction

class ConstantFunction(var constantValue : Float) : PiecewiseTimeFunction {
    override fun read(startingValue: Float, timeInSeconds: Float): Float {
        return constantValue
    }
}