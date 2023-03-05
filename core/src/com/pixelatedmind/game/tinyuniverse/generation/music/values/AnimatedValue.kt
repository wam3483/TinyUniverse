package com.pixelatedmind.game.tinyuniverse.generation.music.values

interface AnimatedValue {
    fun getValue(timeElapsed:Float) : Float
}