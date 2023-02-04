package com.pixelatedmind.game.tinyuniverse.generation.world.model

import com.badlogic.gdx.math.Vector2
import com.pixelatedmind.game.tinyuniverse.graph.DelaunayVoronoiEdge

class WorldPolygonModel(val borderEdges: List<Edge>, var biome: Biome, val delaunayVertex : Vector2) {
    fun flattenBorder() : List<Vector2>{
        val result = mutableListOf<Vector2>()

        val possibleEdges = mutableListOf<Edge>()
        possibleEdges.addAll(borderEdges)
        var end: Vector2
        result.addAll(possibleEdges[0].borderPoints)
        possibleEdges.removeAt(0)

        while(possibleEdges.isNotEmpty()){
            end = result.last()
            val edgeMatch = possibleEdges.first{it.borderPoints.first()==end || it.borderPoints.last()==end}!!
            possibleEdges.remove(edgeMatch)
            if(edgeMatch.borderPoints.last() == end){
                result.addAll(edgeMatch.borderPoints.reversed())
            }else{
                result.addAll(edgeMatch.borderPoints)
            }
        }
        return result.distinct()
    }
    class Edge(val borderPoints:List<Vector2>, val connectingEdge: DelaunayVoronoiEdge, var edgeType: EdgeType, val thickness:Float){
        val adjacentWorldPolygonModels = mutableListOf<WorldPolygonModel>()
    }
    enum class EdgeType{
        None,
        Waterline,
        River,
        Crack
    }
}