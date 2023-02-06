package com.pixelatedmind.game.tinyuniverse.generation.world.model

import com.badlogic.gdx.math.Vector2
import com.pixelatedmind.game.tinyuniverse.graph.DelaunayVoronoiEdge

class WorldPolygonModel(var borderEdges: List<Edge>, var biome: Biome, val delaunayVertex : Vector2) {
    fun flattenBorder() : List<Vector2>{
        val result = mutableListOf<Vector2>()

        val possibleEdges = mutableListOf<Edge>()
        possibleEdges.addAll(borderEdges)
        var end: Vector2
        result.addAll(possibleEdges[0].borderPoints)
        possibleEdges.removeAt(0)

        while(possibleEdges.isNotEmpty()){
            end = result.last()
            val edgeMatch = possibleEdges.first{it.borderPoints.first()==end || it.borderPoints.last()==end}
            possibleEdges.remove(edgeMatch)
            if(edgeMatch.borderPoints.last() == end){
                result.addAll(edgeMatch.borderPoints.reversed())
            }else{
                result.addAll(edgeMatch.borderPoints)
            }
        }
        return result.distinct()
    }
    class Edge(var borderPoints:List<Vector2>, val connectingEdge: DelaunayVoronoiEdge, var edgeType: EdgeType, val thickness:Float,
               val  adjacentWorldPolygonModels : MutableList<WorldPolygonModel>){
        var height : Float = 0f
        var humidity : Float = 0f
    }
    enum class EdgeType{
        None,
        Waterline,
        Lakeline,
        River,
        Crack
    }
}