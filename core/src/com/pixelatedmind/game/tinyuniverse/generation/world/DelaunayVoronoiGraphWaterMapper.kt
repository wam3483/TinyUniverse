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
        val vectorToWorldPolygonModel = mutableMapOf<Vector2, WorldPolygonBiomeModel>()
        graph.flattenDelaunayVertices().forEach{delaunayVertex->
            val worldCellModel = vectorToPolygonWorldData(graph, delaunayVertex)
            if(worldCellModel!=null) {
                vectorToWorldPolygonModel[delaunayVertex] = worldCellModel
            }
        }
        return vectorToWorldPolygonModel
    }

    private fun vectorToPolygonWorldData(graph : DelaunayVoronoiGraph, delaunayVertex:Vector2):WorldPolygonBiomeModel?{
        val edges = graph.getEdges(delaunayVertex)!!
        //discard edges for now, might connect these to a border later?
        if(edges.any{it.voronoiN1 == null || it.voronoiN2 == null}){
            return null
        }

        val correctBorderEdges = mutableListOf<DelaunayVoronoiEdge>()
        val firstEdge = edges[0]
        var currentEdge : DelaunayVoronoiEdge? = firstEdge
        while(currentEdge!=null){
            correctBorderEdges.add(currentEdge)
            //set current edge to the first edge that we haven't found yet, and that shares a vertex with the previous edge
            currentEdge = edges.firstOrNull{potentialEdge->
                //is this edge not part of the path we're building?
                !correctBorderEdges.any{knownEdge-> knownEdge.voronoiN1==potentialEdge.voronoiN1 && knownEdge.voronoiN2==potentialEdge.voronoiN2}
                    &&
                //does this edge share a vertex with the current edge?
                (potentialEdge.voronoiN1==currentEdge!!.voronoiN1 || potentialEdge.voronoiN2==currentEdge!!.voronoiN2
                        || potentialEdge.voronoiN1==currentEdge!!.voronoiN2 || potentialEdge.voronoiN2==currentEdge!!.voronoiN1)}
        }
        ++index
        val biomes = listOf(Biome.Grassland, Biome.Mountains)
        val isLandBiome = landBitmap.getValue(delaunayVertex.x.toInt(),delaunayVertex.y.toInt())
        val biome = biomes[index%biomes.size]
//                when(isLandBiome){
//            true->Biome.Water
//            else->Biome.Unknown
//        }
        return WorldPolygonBiomeModel(biome, correctBorderEdges)
    }
    var index = 0
}