package com.pixelatedmind.game.tinyuniverse.generation.world

class BiomeResolver (val biomes : Map<Biome, BiomeData>){
    fun map(biomeData:BiomeData) : Biome{
        val potentialBiomes = biomes.filter{
            biomeData.humidity>=it.value.humidity &&
            biomeData.height>= it.value.height &&
            biomeData.heat >= it.value.heat
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