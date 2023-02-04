package com.pixelatedmind.game.tinyuniverse.generation.world

import com.pixelatedmind.game.tinyuniverse.generation.world.model.Biome
import com.pixelatedmind.game.tinyuniverse.generation.world.model.BiomeDataModel

class BiomeResolver (val biomes : Map<Biome, BiomeDataModel>){
    fun map(biomeData: BiomeDataModel) : Biome {
        val potentialBiomes = biomes.filter{
            biomeData.humidity>=it.value.humidity &&
            biomeData.height>= it.value.height
        }
        var closestBiome = Biome.Unknown
        var lowestValue = Double.MAX_VALUE
        potentialBiomes.forEach {
            val diff = biomeData.difference(it.value)
            if(diff<lowestValue){
                lowestValue = diff
                closestBiome = it.key
            }
        }
        return closestBiome
    }
}