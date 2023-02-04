package com.pixelatedmind.game.tinyuniverse.generation.world

import com.pixelatedmind.game.tinyuniverse.generation.world.model.Biome
import com.pixelatedmind.game.tinyuniverse.generation.world.model.BiomeDataModel
import com.pixelatedmind.game.tinyuniverse.generation.world.model.WorldModel
import hoten.perlin.Perlin2d
import java.util.*

class WorldGenerator(val biomeResolver:BiomeResolver, val width : Int, val height: Int, val seed:Long) {
    fun generate() : WorldModel?{
        val random = Random(seed)
        val persistence = .5
        val octaves = 8
        val tempMap = generateNoise(persistence, octaves, random.nextInt())
        val heightMap = generateNoise(persistence, octaves, random.nextInt())
        val humidityMap = generateNoise(persistence, octaves, random.nextInt())
        var y = 0
        val biomeCells = mutableListOf<MutableList<Biome>>()
        while(y<tempMap.size){
            var x = 0
            val row = mutableListOf<Biome>()
            biomeCells.add(row)
            while(x<tempMap[0].size){
                val biomeData = BiomeDataModel(heightMap[y][x], humidityMap[y][x])
                val biome = biomeResolver.map(biomeData)
                row.add(biome)
                x++
            }
            y++
        }
        return null
//        return WorldModel(biomeCells)
    }

    private fun generateNoise(persistence:Double, octaves:Int, seed:Int): List<List<Double>> {
        val noise = Perlin2d(persistence, octaves, seed)
        val ary = noise.createTiledArray(width, height)
        val map = ary.map{it.toList()}
        return map
    }
}