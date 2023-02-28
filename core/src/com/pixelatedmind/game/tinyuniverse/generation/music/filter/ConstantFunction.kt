package com.pixelatedmind.game.tinyuniverse.generation.music.filter

class ConstantFunction(var constantValue : Float) : PiecewiseTimeFunction {
    override fun read(startingValue: Float, timeInSeconds: Float): Float {
        return constantValue
    }
}