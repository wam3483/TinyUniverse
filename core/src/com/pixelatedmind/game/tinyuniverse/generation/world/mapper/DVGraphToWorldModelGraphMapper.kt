package com.pixelatedmind.game.tinyuniverse.generation.world.mapper

import com.badlogic.gdx.math.Vector2
import com.pixelatedmind.game.tinyuniverse.generation.world.model.Biome
import com.pixelatedmind.game.tinyuniverse.generation.world.model.WorldPolygonBiomeModel
import com.pixelatedmind.game.tinyuniverse.generation.world.model.WorldPolygonModel
import com.pixelatedmind.game.tinyuniverse.graph.AdjacencyGraphImpl
import com.pixelatedmind.game.tinyuniverse.graph.DelaunayVoronoiEdge
import com.pixelatedmind.game.tinyuniverse.graph.DelaunayVoronoiGraph
import com.pixelatedmind.game.tinyuniverse.graph.Graph

class DVGraphToWorldModelGraphMapper {
    fun map(dvGraph : DelaunayVoronoiGraph) : Graph<WorldPolygonModel> {
        val vectorToWorldPolygonModel = mutableMapOf<Vector2, WorldPolygonModel>()
        //map every delaunay triangle center to a world model
        dvGraph.flattenDelaunayVertices().forEach{delaunayVertex->
            val worldCellModel = vectorToPolygonWorldData(dvGraph, delaunayVertex)
            if(worldCellModel!=null) {
                vectorToWorldPolygonModel[delaunayVertex] = worldCellModel
            }
        }
        val graph = mapResultToAdjancyGraph(vectorToWorldPolygonModel)
        return graph
    }

    private fun mapResultToAdjancyGraph(map:Map<Vector2, WorldPolygonModel>) : Graph<WorldPolygonModel> {
        val graphModel = mutableMapOf<WorldPolygonModel, MutableList<WorldPolygonModel>>()
        //creating adjacency graph between cells
        map.keys.forEach { delaunayVertex ->
            val worldModel = map[delaunayVertex]!!
            val connectedVerts = worldModel.borderEdges.map { getConnectedDelaunayVert(it.connectingEdge, delaunayVertex) }
            worldModel.borderEdges.forEach {
                val otherVertex = getConnectedDelaunayVert(it.connectingEdge, delaunayVertex)
                val adjacentModel = map[otherVertex]
                if(adjacentModel!=null) {
                    it.adjacentWorldPolygonModels.add(adjacentModel)
                }
            }
            val adjacentWorldModels = connectedVerts.map { map[it] }
                    .filterNotNull()
                    .toMutableList()
            graphModel[worldModel] = adjacentWorldModels
        }
        return AdjacencyGraphImpl(graphModel)
    }

    private fun getConnectedDelaunayVert(edge: DelaunayVoronoiEdge, vert: Vector2): Vector2 {
        return if(edge.delaunayN1==vert){
            edge.delaunayN2
        }else{
            edge.delaunayN1
        }
    }

    private fun vectorToPolygonWorldData(graph : DelaunayVoronoiGraph, delaunayVertex: Vector2): WorldPolygonModel?{
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
        val borders = correctBorderEdges.map{
            WorldPolygonModel.Edge(listOf(it.voronoiN1!!,it.voronoiN2!!),it, WorldPolygonModel.EdgeType.None, 1f,
                mutableListOf())
        }
        var worldModel = WorldPolygonModel(borders, Biome.Unknown, delaunayVertex)
        worldModel.borderEdges.forEach{
            it.adjacentWorldPolygonModels.add(worldModel)
        }
        return worldModel
    }
}