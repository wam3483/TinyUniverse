package com.pixelatedmind.game.tinyuniverse.graph

class Edge<T>(val n1 : Node<T>, val n2: Node<T>) {
    fun hasNode(node: Node<T>):Boolean{
        return n1 == node || n2 == node
    }
    override fun equals(other: Any?): Boolean {
        if(other!=null && other is Edge<*>){
            return (n1 == other.n1 && n2== other.n2)
                    ||
                    (n1 == other.n2 && n2 == other.n1)
        }
        return false
    }
}