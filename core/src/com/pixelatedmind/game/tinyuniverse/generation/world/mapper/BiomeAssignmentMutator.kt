package com.pixelatedmind.game.tinyuniverse.generation.world.mapper

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.pixelatedmind.game.tinyuniverse.generation.ModelFilter
import com.pixelatedmind.game.tinyuniverse.generation.world.model.Biome
import com.pixelatedmind.game.tinyuniverse.generation.world.model.WorldPolygonModel
import com.pixelatedmind.game.tinyuniverse.graph.Graph
import hoten.perlin.Perlin2d
import java.util.*

class BiomeAssignmentMutator(val random:Random, private val width:Int, private val height:Int) : ModelFilter<Graph<WorldPolygonModel>> {
    private val heightMap : List<List<Double>>
    private val humidityMap : List<List<Double>>
    private val rectangleBiomeMap : HashMap<Rectangle, Biome>

    init{
        val persistence = .5
        val octaves = 2
        heightMap = generateNoise(persistence, octaves, random.nextInt())
        humidityMap = generateNoise(persistence, octaves, random.nextInt())

        //areas defined in diagram at this url: http://www-cs-students.stanford.edu/~amitp/game-programming/polygon-map-generation/
        //it's a whittaker diagram
        rectangleBiomeMap = hashMapOf(
                Rectangle(0f,0f,1f,1f) to Biome.SubtropicalDesert,
                Rectangle(1f,0f,1f,1f) to Biome.Grassland,
                Rectangle(2f, 0f, 2f, 1f) to Biome.TropicalSeasonalForest,
                Rectangle(4f, 0f,2f,1f) to Biome.TropicalRainForest,
                Rectangle(0f, 1f, 1f, 1f) to Biome.TemperateDesert,
                Rectangle(1f, 1f, 2f, 1f) to Biome.Grassland,
                Rectangle(3f, 1f, 2f, 1f) to Biome.TemperateDeciduousForest,
                Rectangle(5f, 1f, 1f, 1f) to Biome.TemperateRainForest,
                Rectangle(0f, 2f, 2f, 1f) to Biome.TemperateDesert,
                Rectangle(2f, 2f, 2f, 1f) to Biome.Shrubland,
                Rectangle(4f, 2f, 2f, 1f) to Biome.Taiga,
                Rectangle(0f, 3f, 1f, 1f) to Biome.Scorched,
                Rectangle(1f, 3f, 1f, 1f) to Biome.Bare,
                Rectangle(2f, 3f, 1f, 1f) to Biome.Tundra,
                Rectangle(3f, 3f, 3f, 1f) to Biome.Snow
        )
    }

    fun map(graph: Graph<WorldPolygonModel>){
        graph.getVertices().filter{model->model.biome == Biome.Unknown}
                .forEach {
            it.biome = getBiome(it.delaunayVertex.x.toInt(), it.delaunayVertex.y.toInt())
        }
        graph.getVertices().forEach{
            it.borderEdges.forEach { edge ->
                edge.height = getHeight(edge.connectingEdge.voronoiN1!!)
                edge.humidity = getHumidity(edge.connectingEdge.voronoiN1!!)
            }
        }
    }

    fun getHumidity(v:Vector2):Float{
        val x1 = v.x.toInt() % humidityMap[0].size
        val y1 = v.y.toInt() % humidityMap.size
        return humidityMap[y1][x1].toFloat()
    }
    fun getHeight(v:Vector2):Float{
        val x1 = v.x.toInt() % heightMap[0].size
        val y1 = v.y.toInt() % heightMap.size
        return heightMap[y1][x1].toFloat()
    }

    fun getBiome(x:Int, y:Int): Biome {
        val x1 = x % heightMap[0].size
        val y1 = y % heightMap.size
        val e = heightMap[y1][x1]
        val h = humidityMap[y1][x1]
        val vector = Vector2((h*6).toInt().toFloat(), (e*4).toInt().toFloat())
        val biome = rectangleBiomeMap.keys.firstOrNull{
            it.contains(vector)
        }
        if(biome!=null){
            return rectangleBiomeMap[biome]!!
        }
        return Biome.Unknown
    }

    private fun generateNoise(persistence:Double, octaves:Int, seed:Int): List<List<Double>> {
        val noise = Perlin2d(persistence, octaves, seed)
        val ary = noise.createTiledArray(width, height)
        val map = ary.map{it.toList()}
        return map
    }

    override fun applyFilter(value: Graph<WorldPolygonModel>) {
        map(value)
    }
}