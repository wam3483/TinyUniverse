package com.pixelatedmind.game.tinyuniverse.generation

import java.util.*

class TriangleIndexGraph<T>(val nodes:List<T>, val triangleEdges : ShortArray) {

    fun getChildren(nodeValue: T) : List<Node<T>>?{
        val valueIndex = nodes.indexOf(nodeValue)
        if(valueIndex == -1){
            return null
        }
        val indicesOfValueInTriangle = mutableListOf<Int>()
        triangleEdges.forEachIndexed{index,value->
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
                children.add(Node(nodes[triangleEdges[triangleIndex].toInt()]))
            }
            triangleIndex++
        }
    }

    fun getSpanningTree():Node<T>{
        val stack = Stack<T>()
        val visited = mutableSetOf<T>()
        stack.push(nodes[0])
        val graph = Node(nodes[0])
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