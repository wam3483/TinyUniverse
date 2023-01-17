package com.pixelatedmind.game.tinyuniverse.graph

import java.util.*

class Node<T>(val value : T) {
    val children = mutableListOf<Node<T>>()
    override fun equals(other: Any?): Boolean {
        if(other != null && other is Node<*>){
            return value == other.value
        }
        return false
    }

    fun getEdges() : EdgeGraph<T> {
        val stack = Stack<Node<T>>()
        val edges = mutableListOf<Edge<T>>()
        stack.add(this)

        while(stack.isNotEmpty()){
            val node = stack.pop()
            if(edges.none{it.n1 == node}){
                node.children.forEach{child->
                    if(edges.none{it.n1==child}) {
                        val edge = Edge(node, child)
                        edges.add(edge)
                        stack.push(child)
                    }
                }
            }
        }
        return EdgeGraph(edges)
    }

    fun getNodeFor(value:T): Node<T>?{
        return getNodeForRecursive(value, mutableSetOf<T>())
    }
    private fun getNodeForRecursive(value : T, visited:MutableSet<T>) : Node<T>?{
        if(this.value == value){
            return this
        }
        visited.add(this.value)
        children
            .filter{!visited.contains(it.value)}.forEach{
                val result = it.getNodeForRecursive(value, visited)
                if(result != null){
                    return result
                }
            }
        return null
    }
}