package com.pixelatedmind.game.tinyuniverse.graph

class AdjacencyGraph<T>() {
    private val adjacencyList = mutableMapOf<T, MutableList<T>>()
    fun addValue(value:T){
        if(!adjacencyList.contains(value)){
            adjacencyList[value] = mutableListOf<T>()
        }
    }
    fun addEdge(n1:T, n2:T, bidirectionalConnection:Boolean = false){
        addValue(n1)
        addValue(n2)
        adjacencyList[n1]!!.add(n2)
        if(bidirectionalConnection){
            adjacencyList[n2]!!.add(n1)
        }
    }
}