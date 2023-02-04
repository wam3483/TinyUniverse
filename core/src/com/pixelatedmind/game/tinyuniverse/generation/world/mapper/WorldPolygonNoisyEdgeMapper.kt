package com.pixelatedmind.game.tinyuniverse.generation.world.mapper

import com.badlogic.gdx.math.Vector2
import com.pixelatedmind.game.tinyuniverse.generation.world.Edge2
import com.pixelatedmind.game.tinyuniverse.generation.world.LineInterpolator
import com.pixelatedmind.game.tinyuniverse.generation.world.model.WorldPolygonModel
import com.pixelatedmind.game.tinyuniverse.generation.world.model.WorldPolygonBiomeModel
import com.pixelatedmind.game.tinyuniverse.graph.AdjacencyGraphImpl
import com.pixelatedmind.game.tinyuniverse.graph.DelaunayVoronoiEdge
import com.pixelatedmind.game.tinyuniverse.graph.Graph

class WorldPolygonNoisyEdgeMapper(private val lineInterpolator: LineInterpolator) {

    fun map(graph : Map<Vector2, WorldPolygonBiomeModel>) : Graph<WorldPolygonModel> {
//        val edgePointMap = mutableMapOf<DelaunayVoronoiEdge, List<Vector2>>()
        val voronoiEdgeToEdgeModelMap = mutableMapOf<Edge2, WorldPolygonModel.Edge>()

        val vectorToNoisyPolygonMap = mutableMapOf<Vector2, WorldPolygonModel>()
        graph.keys.forEach{delaunayVertex->
            val worldPolygonWithNoisyEdges = noisyEdgesFromWorldPolygonModel2(graph, delaunayVertex, voronoiEdgeToEdgeModelMap)
            if(worldPolygonWithNoisyEdges!=null) {
                vectorToNoisyPolygonMap[delaunayVertex] = worldPolygonWithNoisyEdges
            }
        }

        val graphModel = mutableMapOf<WorldPolygonModel, MutableList<WorldPolygonModel>>()
        //creating adjacency graph between cells
        vectorToNoisyPolygonMap.keys.forEach{delaunayVertex->
            val worldModel = vectorToNoisyPolygonMap[delaunayVertex]!!
            val connectedVerts = worldModel.borderEdges.map {getConnectedDelaunayVert(it.connectingEdge, delaunayVertex)}
            graphModel[worldModel] = connectedVerts.map { vectorToNoisyPolygonMap[it] }
                    .filterNotNull()
                    .toMutableList()
        }
        val resultGraph = AdjacencyGraphImpl(graphModel)
        return resultGraph
    }

    private fun getConnectedDelaunayVert(edge:DelaunayVoronoiEdge, vert:Vector2):Vector2{
        return if(edge.delaunayN1==vert){
            edge.delaunayN2
        }else{
            edge.delaunayN1
        }
    }

    private fun noisyEdgesFromWorldPolygonModel2(graph : Map<Vector2, WorldPolygonBiomeModel>, delaunayVertex:Vector2,
                                                voronoiEdgeToEdgeModelMap : MutableMap<Edge2, WorldPolygonModel.Edge>) : WorldPolygonModel?{
        val model = graph[delaunayVertex]!!
        val voronoiEdges = model.edges

        //map each graph edge to a set of noisy points.
        // use an edge map from voronoi edge so as we traverse the graph we only define each edge once!
        val tempEdge = Edge2()
        val modelEdges = voronoiEdges.map{graphEdge->
            tempEdge.set(graphEdge.voronoiN1!!, graphEdge.voronoiN2!!)
            var edge = voronoiEdgeToEdgeModelMap[tempEdge]
            if(edge==null){
                val borderPoints = lineInterpolator.interpolate(graphEdge, 1f, 0f)
                val newEdge = WorldPolygonModel.Edge(borderPoints,graphEdge, WorldPolygonModel.EdgeType.None,1f)
                voronoiEdgeToEdgeModelMap[Edge2(tempEdge)] = newEdge
                edge = newEdge
            }
            edge!!
        }
        val result = WorldPolygonModel(modelEdges, model.biome, delaunayVertex)

        //add this model to each edge's adjacent list.
        modelEdges.forEach{
            it.adjacentWorldPolygonModels.add(result)
        }
        return result
    }

//    private fun noisyEdgesFromWorldPolygonModel(graph : Map<Vector2, WorldPolygonBiomeModel>, delaunayVertex:Vector2,
//                                                edgePointCache : MutableMap<DelaunayVoronoiEdge, List<Vector2>>) : WorldPolygonModel?{
//
//        val model = graph[delaunayVertex]!!
//        val edges = model.edges
//
//        edges.map{
//
//        }
//
//        val borderSegmentPoints = edges.map{ edge->
//            //getting key a little different since the hashset and equals function for edge won't work for this
//            val key = edgePointCache.keys.firstOrNull{
//                (it.voronoiN1==edge.voronoiN1 && it.voronoiN2==edge.voronoiN2)
//                        ||
//                        (it.voronoiN1==edge.voronoiN2 && it.voronoiN2==edge.voronoiN1)
//            }
//
//            var points : List<Vector2>? = null
//            if(key!=null){
//                points = edgePointCache[key]
//            }
//            if(points == null){
//                points = lineInterpolator.interpolate(edge, 1f, 0f)
//                edgePointCache[edge] = points
//            }
//            Pair(edge,points!!)
//        }
//        //TODO need a cache from delaunayVoronoiEdge to WorldPolygonModel.Edge. It's important edge references
//        //be shared among polygons!!! right now each edge reference is unique and it's hindering assigning waterlines
//        val modelEdges = borderSegmentPoints.map {graphEdgeAndPoints->
//            WorldPolygonModel.Edge(graphEdgeAndPoints.second, graphEdgeAndPoints.first,
//                    WorldPolygonModel.EdgeType.None,
//                    1f)
//        }
//
//        val result = WorldPolygonModel(modelEdges, model.biome, delaunayVertex)
//        return result
//    }
}