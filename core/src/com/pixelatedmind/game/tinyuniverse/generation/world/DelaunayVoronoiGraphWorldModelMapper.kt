package com.pixelatedmind.game.tinyuniverse.generation.world

import com.badlogic.gdx.math.Vector2
import com.pixelatedmind.game.tinyuniverse.graph.AdjacencyGraphImpl
import com.pixelatedmind.game.tinyuniverse.graph.DelaunayVoronoiEdge
import com.pixelatedmind.game.tinyuniverse.graph.DelaunayVoronoiGraph
import com.pixelatedmind.game.tinyuniverse.graph.VoronoiGraph

class DelaunayVoronoiGraphWorldModelMapper {
    fun map(graph : DelaunayVoronoiGraph){
        graph.flattenDelaunayVertices().forEach{delaunayVertex->
            val edges = graph.getEdges(delaunayVertex)

        }
    }

    fun noisyLine(edge : DelaunayVoronoiEdge):List<Vector2>{
        val list = mutableListOf<Vector2>()
        if(edge.voronoiN2!=null && edge.voronoiN1!=null) {
            return listOf(edge.voronoiN1!!, edge.voronoiN2!!)
        }
        return listOf()
    }
}