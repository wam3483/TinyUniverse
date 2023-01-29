package com.pixelatedmind.game.tinyuniverse.graph

import com.badlogic.gdx.math.DelaunayTriangulator
import com.badlogic.gdx.math.Vector2
import java.util.*

class DelaunayTriangulationAdjacencyGraphMapper {
    fun map(pointCloud:List<Vector2>):Graph<Vector2>{
        val triangulator = DelaunayTriangulator()
        val triangleVertices = triangulator.computeTriangles(pointsToFloatArray(pointCloud), false)
        val graph = AdjacencyGraphImpl<Vector2>()
        val numTriangles = triangleVertices.size / 3
        var i = 0
        while(i<numTriangles){
            val i1 = triangleVertices[i]
            val i2 = triangleVertices[i+1]
            val i3 = triangleVertices[i+2]
            graph.addEdge(pointCloud[i1.toInt()], pointCloud[i2.toInt()])
            graph.addEdge(pointCloud[i2.toInt()], pointCloud[i3.toInt()])
            graph.addEdge(pointCloud[i3.toInt()], pointCloud[i1.toInt()])
            i += 3
        }
        return graph
    }

    private fun pointsToFloatArray(vertices:List<Vector2>) : FloatArray {
        val ary = FloatArray(vertices.size*2)
        vertices.forEachIndexed{ index, v->
            val i2 = index*2
            ary[i2] = v.x
            ary[i2+1] = v.y
        }
        return ary
    }

    fun mapVoronoi(delaunayGraph:Graph<Vector2>) : Graph<DelaunayVoronoiEdge<Vector2>>{
        val vertices = delaunayGraph.getVertices()
        val stack = Stack<Vector2>()
        stack.add(vertices.first())
        val tempTri = TriangleVectorImpl()
        while(stack.isNotEmpty()){
            val node = stack.pop()
            val children = delaunayGraph.getChildren(node)!!
            var i =0
            while(i<children.size-2){
                val v1 = children[i]
                val v2 = children[i+1]
                val v3 = children[i+2]
                tempTri.set(v1,v2,v3)

                val t
            }
        }
    }
}