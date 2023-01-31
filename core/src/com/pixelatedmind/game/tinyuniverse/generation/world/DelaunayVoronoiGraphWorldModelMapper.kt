package com.pixelatedmind.game.tinyuniverse.generation.world

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.pixelatedmind.game.tinyuniverse.graph.*
import java.util.*

class DelaunayVoronoiGraphWorldModelMapper(val lineInterpolator: LineInterpolator) {

    val biomes = listOf(Biome.Grassland,Biome.Tundra,Biome.Desert, Biome.Jungle, Biome.Mountains, Biome.Water,Biome.Forest)

    fun map(graph : DelaunayVoronoiGraph) : Graph<WorldPolygonModel> {

        val graphModel = mutableMapOf<WorldPolygonModel, MutableList<WorldPolygonModel>>()
        val vectorToWorldPolygonModel = mutableMapOf<Vector2, WorldPolygonModel>()

        val edgePointMap = mutableMapOf<DelaunayVoronoiEdge, List<Vector2>>()
        graph.flattenDelaunayVertices().forEach{delaunayVertex->
            val worldCellModel = vectorToPolygonWorldData(graph, delaunayVertex, edgePointMap)
            if(worldCellModel!=null) {
                vectorToWorldPolygonModel[delaunayVertex] = worldCellModel
            }
        }

        graph.flattenDelaunayVertices().forEach{delaunayVertex->
            val worldModel = vectorToWorldPolygonModel[delaunayVertex]
            if(worldModel!=null) {
                val edges = graph.getEdges(delaunayVertex)!!
                val connectedVerts = edges.map {
                    if (it.delaunayN1 == delaunayVertex) {
                        it.delaunayN2
                    }
                    it.delaunayN1
                }
                graphModel[worldModel] = connectedVerts.map { vectorToWorldPolygonModel[it] }
                        .filterNotNull()
                        .toMutableList()
            }
        }
        return AdjacencyGraphImpl(graphModel)
    }

    private fun vectorToPolygonWorldData(graph : DelaunayVoronoiGraph, delaunayVertex:Vector2,
                                         edgePointCache : MutableMap<DelaunayVoronoiEdge, List<Vector2>>):WorldPolygonModel?{
        val edges = graph.getEdges(delaunayVertex)!!
        if(edges.any{it.voronoiN1 == null || it.voronoiN2 == null}){
            return null
        }

        val correctBorderEdges = mutableListOf<DelaunayVoronoiEdge>()
        val firstEdge = edges[0]
        var currentEdge : DelaunayVoronoiEdge? = firstEdge
        while(currentEdge!=null){
            correctBorderEdges.add(currentEdge)
            currentEdge = edges.firstOrNull{!correctBorderEdges.any{e->e.voronoiN1==it.voronoiN1 && e.voronoiN2==it.voronoiN2} &&
                    (it.voronoiN1==currentEdge!!.voronoiN1 || it.voronoiN2==currentEdge!!.voronoiN2)}
        }

        val borderSegmentPoints = edges.map{ edge->
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
                points = lineInterpolator.interpolate(edge, 1f, 0f)
                edgePointCache[edge] = points
            }
            points!!
        }
        val modelEdges = borderSegmentPoints.map {polygonEdge->
            WorldPolygonModel.Edge(polygonEdge, WorldPolygonModel.EdgeType.None, 1f) }

        val model = WorldPolygonModel(modelEdges, biomes[index % biomes.size])
        ++index
        return model
    }

    var index = 0

}