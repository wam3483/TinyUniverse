package com.pixelatedmind.game.tinyuniverse.generation.world.mapper

import com.pixelatedmind.game.tinyuniverse.generation.ModelFilter
import com.pixelatedmind.game.tinyuniverse.generation.world.Edge2
import com.pixelatedmind.game.tinyuniverse.generation.world.LineInterpolator
import com.pixelatedmind.game.tinyuniverse.generation.world.model.Biome
import com.pixelatedmind.game.tinyuniverse.generation.world.model.WorldPolygonModel
import com.pixelatedmind.game.tinyuniverse.generation.world.model.isWater
import com.pixelatedmind.game.tinyuniverse.graph.Graph

class WorldPolygonNoisyEdgeMutator(private val lineInterpolator: LineInterpolator) : ModelFilter<Graph<WorldPolygonModel>> {

    fun map(graph : Graph<WorldPolygonModel>) {
        val edgeCache = mutableMapOf<Edge2, WorldPolygonModel.Edge>()
        graph.getVertices().forEach{key-> noisyEdgesFromWorldPolygonModel(key, edgeCache) }
    }

    private fun noisyEdgesFromWorldPolygonModel(key:WorldPolygonModel, voronoiEdgeToEdgeModelMap : MutableMap<Edge2, WorldPolygonModel.Edge>){
        val tempEdge = Edge2()

        // map each graph edge to a set of noisy points.
        // use an edge map from voronoi edge so as we traverse the graph we only define each edge once
        val modelEdges = key.borderEdges.map{
            val graphEdge = it.connectingEdge
            tempEdge.set(graphEdge.voronoiN1!!, graphEdge.voronoiN2!!)
            var edge = voronoiEdgeToEdgeModelMap[tempEdge]
            if(edge == null)
            {
                edge = it
                voronoiEdgeToEdgeModelMap[Edge2(tempEdge)] = it
                var lineResolution = 1f
                var lineSmoothness: Float
                if(key.biome == Biome.Lake){
                    lineSmoothness = .2f
                }
                else if(key.biome == Biome.Water){
                    lineSmoothness = 0f
                }
                else {
                    lineSmoothness = .5f
                }
                val borderPoints = lineInterpolator.interpolate(graphEdge, lineResolution, lineSmoothness)
                it.borderPoints = borderPoints
            }

            edge!!
        }
        key.borderEdges = modelEdges
    }

    override fun applyFilter(value: Graph<WorldPolygonModel>) {
        map(value)
    }
}