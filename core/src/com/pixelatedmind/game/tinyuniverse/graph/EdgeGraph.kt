package com.pixelatedmind.game.tinyuniverse.graph

class EdgeGraph<T> (graphEdges : List<Edge<T>>){
    val edges = graphEdges.toMutableList()

    private fun getAllValuesAsMap():Set<T>{
        val hashset = mutableSetOf<T>()
        edges.forEach{
            hashset.add(it.n1)
            hashset.add(it.n2)
        }
        return hashset
    }

    fun add(edge : Edge<T>){
        edges.add(edge)
    }

    fun getAllValues() : List<T>{
        return getAllValuesAsMap().map{it}
    }

    fun contains(edge : Edge<T>) : Boolean{
        return edges.contains(edge)
    }

    fun contains(value : T) : Boolean{
        return getAllValues().contains(value)
    }
}