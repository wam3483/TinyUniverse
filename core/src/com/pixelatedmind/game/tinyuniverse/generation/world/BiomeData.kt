package com.pixelatedmind.game.tinyuniverse.generation.world

import kotlin.math.abs

class BiomeData(val heat:Double, val height:Double, val humidity:Double) {
    fun difference(other:BiomeData):Double{
        return abs(heat-other.heat) +
                abs(height - other.height) +
                abs(humidity-other.humidity)
    }
}