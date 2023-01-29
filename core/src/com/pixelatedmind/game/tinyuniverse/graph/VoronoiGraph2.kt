package com.pixelatedmind.game.tinyuniverse.graph

import com.badlogic.gdx.math.Vector2
import java.util.*

class VoronoiGraph2(val delaunayGraph : Graph<Vector2>) {
    init{
        val stack = Stack<Vector2>()
        val visited = mutableSetOf<Vector2>()
        val vectorRef = mutableMapOf<Vector2,Vector2>()
        val firstNode = delaunayGraph.getVertices().first()
        stack.push(firstNode)
        while(stack.isNotEmpty()){
            val node = stack.pop()
            visited.add(node)
            val children = delaunayGraph.getChildren(node)!!
            children.filter { !visited.contains(it) }.forEach{
                stack.push(it)
            }
            var i = 0
            //Each node in a delaunay triangulation graph will have at least 3 children (because there must be at least one triangle.)
            //consider 2 triangles at a time, and create a node for a new graph defined by the centers of 2 triangles in the delaunay triangulation, and the
            //delaunay graph vertices which intersect a line drawn between these triangle centers.
            val tempVector1 = Vector2()
            val tempVector2 = Vector2()
            val triangle1 = TriangleVectorImpl()
            val triangle2 = TriangleVectorImpl()
            while(i<children.size-2){
                val t2 = children[i]
                val t3 = children[i+1]
                val t4 = children[i+2]
                triangle1.set(node,t2,t3)
                triangle2.set(node,t3,t4)
                triangle1.centroid(tempVector1)
                triangle2.centroid(tempVector2)
                i++
            }
        }
    }

    class Node<T>(
        val delaunayEdge : Edge<T>,
        val voronoiEdge : Edge<T>
    )
}