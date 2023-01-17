package com.pixelatedmind.game.tinyuniverse.graph

import com.badlogic.gdx.math.DelaunayTriangulator
import java.util.*

class TriangleMeshGraph<T>(val vertexValues:List<T>, vertexPositions:FloatArray){//, val triangleMeshEdges : ShortArray) {

    private val triangulator = DelaunayTriangulator()
    private var triangleMeshEdges : ShortArray
    init{
        val triangleGraphIndices = triangulator.computeTriangles(vertexPositions,false)
        triangleMeshEdges = ShortArray(triangleGraphIndices.size)
        var index =0
        repeat(triangleGraphIndices.size){
            triangleMeshEdges[index] = triangleGraphIndices.get(index)
            index++
        }
    }

    fun getChildren(nodeValue: T) : List<Node<T>>?{
        val valueIndex = vertexValues.indexOf(nodeValue)
        if(valueIndex == -1){
            return null
        }
        val indicesOfValueInTriangle = mutableListOf<Int>()
        triangleMeshEdges.forEachIndexed{ index, value->
            if(value==valueIndex.toShort()){
                indicesOfValueInTriangle.add(index)
            }
        }
        val children = mutableListOf<Node<T>>()
        indicesOfValueInTriangle.forEach{getChildrenFromIndex(it.toShort(), children)}
        return children
    }

    private fun getChildrenFromIndex(index:Short, children:MutableList<Node<T>>){
        var triangleIndex = index - index % 3
        val nodeIndex = index.toInt()
        repeat(3){
            if(triangleIndex!=nodeIndex){
                children.add(Node(vertexValues[triangleMeshEdges[triangleIndex].toInt()]))
            }
            triangleIndex++
        }
    }

    fun getSpanningTree(): Node<T> {
        val stack = Stack<T>()
        val visited = mutableSetOf<T>()
        stack.push(vertexValues[0])
        val graph = Node(vertexValues[0])
        while(stack.isNotEmpty()){
            val nodeValue = stack.pop()
            visited.add(nodeValue)
            val children = getChildren(nodeValue)
            val graphNode = graph.getNodeFor(nodeValue)
            if(children!=null && graphNode!=null){
                val unvisitedChildren = children.filter { !visited.contains(it.value) }
                unvisitedChildren.forEach {
                    visited.add(it.value)
                    //we found an edge in our spanning tree.
                    graphNode.children.add(Node(it.value))
                    stack.push(it.value)
                }
            }
        }
        return graph
    }
}