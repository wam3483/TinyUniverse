package com.pixelatedmind.game.tinyuniverse.generation.math

interface PiecewiseTimeFunction {
    fun read(startingValue: Float, timeInSeconds: Float) : Float
}