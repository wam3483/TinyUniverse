package com.pixelatedmind.game.tinyuniverse.graph

import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Vector2
import java.util.*

class DelaunayVoronoiGraph(private val map : Map<Vector2, List<DelaunayVoronoiEdge>>) {
    fun flattenDelaunayVertices():List<Vector2>{
        return map.keys.toList()
    }
    fun getEdges(vector:Vector2) : List<DelaunayVoronoiEdge>?{
        return map[vector]
    }
    fun test(){
        val stack = Stack<Vector2>()
        stack.push(map.keys.first())
        val visited = mutableSetOf<Vector2>()

        while(stack.isNotEmpty()){
            val v1 = stack.pop()
            visited.add(v1)
            val voronoiEdges = map[v1]!!
            voronoiEdges.forEach{
                val child = when(v1){
                    it.delaunayN1 -> it.delaunayN2
                    else -> it.delaunayN1
                }
                if(!visited.contains(child)) {
                    stack.push(child)
                }
            }
        }
    }
}