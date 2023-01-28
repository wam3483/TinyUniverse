package com.pixelatedmind.game.tinyuniverse.graph

interface Graph<T> {
    fun getChildren(value:T):List<T>?
    fun addEdge(n1:T, n2:T)
}