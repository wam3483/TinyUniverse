package com.pixelatedmind.game.tinyuniverse.generation.world

import com.badlogic.gdx.math.Vector2
import com.pixelatedmind.game.tinyuniverse.graph.*
import com.pixelatedmind.game.tinyuniverse.maps.tiled.Bitmap
import java.util.*

class DelaunayVoronoiGraphWorldModelMapper(val borderNoise: LineInterpolator, val landmassBitmap : Bitmap) {

    private val biomes = listOf(Biome.Grassland,Biome.Tundra,Biome.Desert, Biome.Jungle, Biome.Mountains, Biome.Water,Biome.Forest)

    fun map(graph : DelaunayVoronoiGraph) : Graph<WorldPolygonModel> {
        val waterLandMapper = DelaunayVoronoiGraphWaterMapper(landmassBitmap)
        val vectorWorldPolygonModelMap = waterLandMapper.map(graph)
        val edgeDetailMapper = WorldPolygonEdgeDetailMapper(borderNoise)
        val graph = edgeDetailMapper.map(vectorWorldPolygonModelMap)

        return graph
    }

    private fun vectorToPolygonWorldData(graph : DelaunayVoronoiGraph, delaunayVertex:Vector2,
                                         edgePointCache : MutableMap<DelaunayVoronoiEdge, List<Vector2>>):WorldPolygonModel?{
        val edges = graph.getEdges(delaunayVertex)!!
        if(edges.any{it.voronoiN1 == null || it.voronoiN2 == null}){
            return null
        }

        val borderSegmentPoints = edges.map{ edge->
            //getting key a little different since the hashset and equals function for edge won't work for this
            val key = edgePointCache.keys.firstOrNull{
                (it.voronoiN1==edge.voronoiN1 && it.voronoiN2==edge.voronoiN2)
                        ||
                (it.voronoiN1==edge.voronoiN2 && it.voronoiN2==edge.voronoiN1)
            }

            var points : List<Vector2>? = null
            if(key!=null){
                points = edgePointCache[key]
            }
            if(points == null){
                points = borderNoise.interpolate(edge, 1f, 0f)
                edgePointCache[edge] = points
            }
            Pair(edge,points!!)
        }
        val modelEdges = borderSegmentPoints.map {graphEdgeAndPoints->
            WorldPolygonModel.Edge(graphEdgeAndPoints.second, graphEdgeAndPoints.first, WorldPolygonModel.EdgeType.None, 1f) }

        val model = WorldPolygonModel(modelEdges, biomes[index % biomes.size])
        ++index
        return model
    }

    var index = 0

}