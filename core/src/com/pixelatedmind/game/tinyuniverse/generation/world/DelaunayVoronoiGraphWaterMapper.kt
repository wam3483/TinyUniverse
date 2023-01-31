package com.pixelatedmind.game.tinyuniverse.generation.world

import com.badlogic.gdx.math.Vector2
import com.pixelatedmind.game.tinyuniverse.generation.world.model.WorldPolygonBiomeModel
import com.pixelatedmind.game.tinyuniverse.graph.DelaunayVoronoiEdge
import com.pixelatedmind.game.tinyuniverse.graph.DelaunayVoronoiGraph
import com.pixelatedmind.game.tinyuniverse.maps.tiled.Bitmap

/***
 * Assigns voronoi regions as either water biome, or unknown by querying a bitmap for true/false values respectively.
 * Land bitmap should return true to indicate region should be land, and false for water.
 */
class DelaunayVoronoiGraphWaterMapper(val landBitmap: Bitmap) {

    fun map(graph : DelaunayVoronoiGraph):Map<Vector2, WorldPolygonBiomeModel>{
        val edgePointMap = mutableMapOf<DelaunayVoronoiEdge, List<Vector2>>()
        val vectorToWorldPolygonModel = mutableMapOf<Vector2, WorldPolygonBiomeModel>()
        graph.flattenDelaunayVertices().forEach{delaunayVertex->
            val worldCellModel = vectorToPolygonWorldData(graph, delaunayVertex, edgePointMap)
            if(worldCellModel!=null) {
                vectorToWorldPolygonModel[delaunayVertex] = worldCellModel
            }
        }
        return vectorToWorldPolygonModel
    }

    private fun vectorToPolygonWorldData(graph : DelaunayVoronoiGraph, delaunayVertex:Vector2,
                                         edgePointCache : MutableMap<DelaunayVoronoiEdge, List<Vector2>>):WorldPolygonBiomeModel?{
        val edges = graph.getEdges(delaunayVertex)!!
        if(edges.any{it.voronoiN1 == null || it.voronoiN2 == null}){
            return null
        }

        val correctBorderEdges = mutableListOf<DelaunayVoronoiEdge>()
        val firstEdge = edges[0]
        var currentEdge : DelaunayVoronoiEdge? = firstEdge
        while(currentEdge!=null){
            correctBorderEdges.add(currentEdge)
            currentEdge = edges.firstOrNull{!correctBorderEdges.any{
                e->e.voronoiN1==it.voronoiN1 && e.voronoiN2==it.voronoiN2}
                    &&
                (it.voronoiN1==currentEdge!!.voronoiN1 || it.voronoiN2==currentEdge!!.voronoiN2)}
        }

        val isLandBiome = landBitmap.getValue(delaunayVertex.x.toInt(),delaunayVertex.y.toInt())
        val biome = when(isLandBiome){
            true->Biome.Unknown
            else->Biome.Water
        }
        return WorldPolygonBiomeModel(biome, correctBorderEdges)
    }
}