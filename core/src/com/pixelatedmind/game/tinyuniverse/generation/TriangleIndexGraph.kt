package com.pixelatedmind.game.tinyuniverse.generation

import java.util.*

class TriangleIndexGraph<T>(val nodes:List<T>, val triangleEdges : ShortArray) {

    fun getChildren(value: T) : List<Node<T>>?{
        val valueIndex = nodes.indexOf(value)
        if(valueIndex == -1){
            return null
        }
        val everyOcurranceOfValueInTriangle = mutableListOf<Int>()
        triangleEdges.forEachIndexed{index,value->
            if(value==valueIndex.toShort()){
                everyOcurranceOfValueInTriangle.add(index)
            }
        }
        val children = mutableListOf<Node<T>>()
        everyOcurranceOfValueInTriangle.forEach{getChildrenFromIndex(it.toShort(), children)}
        return children
    }

    private fun getChildrenFromIndex(index:Short, children:MutableList<Node<T>>){
        var triangleIndex = index / 3
        val nodeIndex = triangleIndex - index % 3
        repeat(3){
            if(triangleIndex!=nodeIndex){
                children.add(Node(nodes[triangleIndex]))
            }
            triangleIndex++
        }
    }

    fun getSpanningTree(value:T):Node<T>{
        val stack = Stack<T>()
        val visited = mutableSetOf<T>()
        stack.push(nodes[0])
        visited.add(nodes[0])
        val graph = Node(nodes[0])
        while(stack.isNotEmpty()){
            val nodeValue = stack.pop()
            val children = getChildren(nodeValue)
            val unvisitedChild = children?.firstOrNull { !visited.contains(it.value) }
            if(unvisitedChild!=null)
            {
                //we found an edge in our spanning tree.
                val graphNode = graph.getNodeFor(nodeValue)!!
                val edgeEnd = Node(unvisitedChild.value)
                graphNode.children.add(edgeEnd)
                edgeEnd.children.add(graphNode)
                visited.add(unvisitedChild.value)
                stack.push(unvisitedChild.value)
            }
        }
        return graph
    }

//    fun getMinimumSpanningTreeRecursive(msp: MutableList<Node<T>>){
//        val node = nodes[0]
//        val edges = getEdges(node)
//        if(msp.contains()){
//
//        }
//    }
}