package com.pixelatedmind.game.tinyuniverse.graph

import com.badlogic.gdx.math.Vector2
import java.util.*

class DelaunayVoronoiGraph(private val map : Map<Vector2, List<DelaunayVoronoiEdge>>) {
    fun flattenDelaunayVertices():List<Vector2>{
        return map.keys.toList()
    }

    fun flattenVoronoiVertices():List<Vector2>{
        val hashset = mutableSetOf<Vector2>()
        val result = mutableListOf<Vector2>()
        map.keys.forEach{
            val list = map[it]!!
            list.forEach{point->
                if(point.voronoiN1!=null && !hashset.contains(point.voronoiN1!!)) {
                    result.add(point.voronoiN1!!)
                    hashset.add(point.voronoiN1!!)
                }
                if(point.voronoiN2!=null && hashset.contains(point.voronoiN2!!)){
                    result.add(point.voronoiN2!!)
                    hashset.add(point.voronoiN2!!)
                }
            }
        }
        return result
    }

    fun getCenterOfVoronoiSites():List<Vector2>{
        val result = map.keys.map{key->
            findVoronoiCenter(map[key]!!)
        }
        return result
    }

    private fun findVoronoiCenter(voronoiCellEdges1:List<DelaunayVoronoiEdge>):Vector2{
        val vCellEdges1Points = voronoiCellEdges1.map{it.voronoiN1}.toMutableList()
        vCellEdges1Points.add(voronoiCellEdges1.last().voronoiN2)
        val sum = Vector2()
        vCellEdges1Points.filterNotNull().forEach{
            sum.add(it)
        }
        sum.scl(1/vCellEdges1Points.size.toFloat())
        return sum
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