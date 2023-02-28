package com.pixelatedmind.game.tinyuniverse.generation.music.filter

interface PiecewiseTimeFunction {
    fun read(startingValue: Float, timeInSeconds: Float) : Float
}