package com.pixelatedmind.game.tinyuniverse.generation.world

import com.pixelatedmind.game.tinyuniverse.graph.AdjacencyGraphImpl
import com.pixelatedmind.game.tinyuniverse.graph.VoronoiGraph

class VoronoiToWorldModelMapper<T> {
    fun map(graph:VoronoiGraph<T>):WorldModel{
        val worldModelGraph = AdjacencyGraphImpl<WorldPolygonModel>()
        graph.getVoronoiCells()
        return WorldModel(worldModelGraph)
    }
}