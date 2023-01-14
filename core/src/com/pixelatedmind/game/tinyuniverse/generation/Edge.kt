package com.pixelatedmind.game.tinyuniverse.generation

class Edge<T>(val n1 :Node<T>, val n2:Node<T>) {
    override fun equals(other: Any?): Boolean {
        if(other!=null && other is Edge<*>){
            return (n1 == other.n1 && n1 == other.n2)
                    ||
                    (n2 == other.n1 && n2 == other.n2)
        }
        return false
    }
}