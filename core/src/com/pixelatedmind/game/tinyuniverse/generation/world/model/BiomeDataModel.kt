package com.pixelatedmind.game.tinyuniverse.generation.world.model

import kotlin.math.abs

class BiomeDataModel(val height:Double, val humidity:Double) {
    fun difference(other: BiomeDataModel) : Double{
        return abs(height - other.height) +
                abs(humidity - other.humidity)
    }
}