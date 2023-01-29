package com.pixelatedmind.game.tinyuniverse.graph

class Edge<T>(var n1 : T, var n2: T) {
    fun hasNode(node: Node<T>):Boolean{
        return n1 == node || n2 == node
    }

    fun set(n1:T, n2:T) : Edge<T>{
        this.n1 = n1
        this.n2 = n2
        return this
    }

    override fun hashCode(): Int {
        return n1.hashCode() + n2.hashCode()
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