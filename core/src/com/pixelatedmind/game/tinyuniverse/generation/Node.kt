package com.pixelatedmind.game.tinyuniverse.generation

class Node<T>(val value : T) {
    val children = mutableListOf<Node<T>>()
    override fun equals(other: Any?): Boolean {
        if(other != null && other is Node<*>){
            return value == other.value
        }
        return false
    }
    fun getNodeFor(value : T) : Node<T>?{
        if(this.value == value){
            return this
        }
        children.forEach{
            val result = it.getNodeFor(value)
            if(result != null){
                return result
            }
        }
        return null
    }
}