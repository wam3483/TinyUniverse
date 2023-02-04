package com.pixelatedmind.game.tinyuniverse.generation.world.mapper

import com.badlogic.gdx.math.MathUtils.random
import com.badlogic.gdx.math.Vector2
import com.pixelatedmind.game.tinyuniverse.generation.world.model.Biome
import com.pixelatedmind.game.tinyuniverse.generation.world.LineInterpolator
import com.pixelatedmind.game.tinyuniverse.generation.world.model.WorldPolygonModel
import com.pixelatedmind.game.tinyuniverse.graph.*
import com.pixelatedmind.game.tinyuniverse.maps.tiled.Bitmap
import java.util.*

class DelaunayVoronoiGraphWorldModelMapper(val borderNoise: LineInterpolator, val landmassBitmap : Bitmap, random : Random) {

    fun map(graph : DelaunayVoronoiGraph) : Graph<WorldPolygonModel> {

        val waterLandMapper = DelaunayVoronoiGraphLandMassMapper(landmassBitmap)
        val vectorWorldPolygonModelMap = waterLandMapper.map(graph)

        val edgeDetailMapper = WorldPolygonNoisyEdgeMapper(borderNoise)
        val graph = edgeDetailMapper.map(vectorWorldPolygonModelMap)

        val biomeMapper = BiomeMapper(random,400,400)
        graph.getVertices().filter{vert->vert.biome == Biome.Unknown}
                .forEach{
                    var d = it.borderEdges.first().connectingEdge.delaunayN1
                    it.biome = biomeMapper.getBiome(d.x.toInt(),d.y.toInt())
                }

        val waterlineMapper = WaterlineEdgeMapper()
        waterlineMapper.map(graph)

        return graph
    }
}