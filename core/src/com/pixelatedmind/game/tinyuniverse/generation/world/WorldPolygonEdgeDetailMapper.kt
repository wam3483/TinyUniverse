package com.pixelatedmind.game.tinyuniverse.generation.world

import com.badlogic.gdx.math.Vector2
import com.pixelatedmind.game.tinyuniverse.generation.world.model.WorldPolygonBiomeModel
import com.pixelatedmind.game.tinyuniverse.graph.AdjacencyGraphImpl
import com.pixelatedmind.game.tinyuniverse.graph.DelaunayVoronoiEdge
import com.pixelatedmind.game.tinyuniverse.graph.Graph

class WorldPolygonEdgeDetailMapper(val lineInterpolator: LineInterpolator) {

    fun map(graph : Map<Vector2, WorldPolygonBiomeModel>) : Graph<WorldPolygonModel> {
        val edgePointMap = mutableMapOf<DelaunayVoronoiEdge, List<Vector2>>()
        val vectorToNoisyPolygonMap = mutableMapOf<Vector2, WorldPolygonModel>()

//        graph.keys.forEach{
//            val model = graph[it]!!
//            model.edges = ensureEdgePathIsSequential(model.edges)
//        }

        graph.keys.forEach{delaunayVertex->
            val worldPolygonWithNoisyEdges = noisyEdgesFromWorldPolygonModel(graph, delaunayVertex, edgePointMap)
            if(worldPolygonWithNoisyEdges!=null) {
                vectorToNoisyPolygonMap[delaunayVertex] = worldPolygonWithNoisyEdges
            }
        }

        val graphModel = mutableMapOf<WorldPolygonModel, MutableList<WorldPolygonModel>>()
        //creating adjacency graph between cells
        vectorToNoisyPolygonMap.keys.forEach{delaunayVertex->
            val worldModel = vectorToNoisyPolygonMap[delaunayVertex]!!
            val worldPolygonData = graph[delaunayVertex]!!
            val connectedVerts = worldModel.borderEdges.map {
                if (it.connectingEdge.delaunayN2 == delaunayVertex) {
                    it.connectingEdge.delaunayN1
                }else {
                    it.connectingEdge.delaunayN2
                }
            }
            graphModel[worldModel] = connectedVerts.map { vectorToNoisyPolygonMap[it] }
                    .filterNotNull()
                    .toMutableList()
        }

        return AdjacencyGraphImpl(graphModel)
    }

    private fun ensureEdgePathIsSequential(edges:List<DelaunayVoronoiEdge>) : List<DelaunayVoronoiEdge>{
        val correctBorderEdges = mutableListOf<DelaunayVoronoiEdge>()
        val firstEdge = edges[0]
        var currentEdge : DelaunayVoronoiEdge? = firstEdge
        while(currentEdge!=null){
            correctBorderEdges.add(currentEdge)
            currentEdge = edges.firstOrNull{!correctBorderEdges.any{e->e.voronoiN1==it.voronoiN1 && e.voronoiN2==it.voronoiN2} &&
                    (it.voronoiN1==currentEdge!!.voronoiN1 || it.voronoiN2==currentEdge!!.voronoiN2)}
        }
        return correctBorderEdges
    }

    private fun noisyEdgesFromWorldPolygonModel(graph : Map<Vector2, WorldPolygonBiomeModel>, delaunayVertex:Vector2,
                                                edgePointCache : MutableMap<DelaunayVoronoiEdge, List<Vector2>>) : WorldPolygonModel?{
        val model = graph[delaunayVertex]!!
        val edges = model.edges

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
                points = lineInterpolator.interpolate(edge, 1f, 0f)
                edgePointCache[edge] = points
            }
            Pair(edge,points!!)
        }
        val modelEdges = borderSegmentPoints.map {graphEdgeAndPoints->
            WorldPolygonModel.Edge(graphEdgeAndPoints.second, graphEdgeAndPoints.first,
                    WorldPolygonModel.EdgeType.None,
                    1f) }

        val result = WorldPolygonModel(modelEdges, model.biome)
        return result
    }
}