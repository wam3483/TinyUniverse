package com.pixelatedmind.game.tinyuniverse.generation.music.values

class ConstantValue(val value : Float) : NormalizedValue {
    override fun getValue(timeElapsed: Float) : Float{
        return value
    }
}