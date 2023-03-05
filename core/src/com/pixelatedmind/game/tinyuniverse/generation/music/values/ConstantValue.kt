package com.pixelatedmind.game.tinyuniverse.generation.music.values

class ConstantValue(val value : Float) : AnimatedValue {
    override fun getValue(timeElapsed: Float) : Float{
        return value
    }
}