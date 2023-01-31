package com.pixelatedmind.game.tinyuniverse.graph

class AdjacencyGraphImpl<T>(map:Map<T,MutableList<T>> = mapOf()) : Graph<T>{
    private val adjacencyList = map.toMutableMap()
    fun addValue(value:T){
        if(!adjacencyList.contains(value)){
            adjacencyList[value] = mutableListOf<T>()
        }
    }
    fun getAllValues():List<T>{
        return adjacencyList.keys.toList()
    }

    override fun getChildren(value:T):List<T>?{
        return adjacencyList[value]
    }
    override fun addEdge(n1:T, n2:T){
        addValue(n1)
        addValue(n2)
        adjacencyList[n1]!!.add(n2)
    }

    override fun getVertices(): List<T> {
        return adjacencyList.keys.toList()
    }
}