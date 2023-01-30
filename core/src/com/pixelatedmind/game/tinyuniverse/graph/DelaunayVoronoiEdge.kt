package com.pixelatedmind.game.tinyuniverse.graph

import com.badlogic.gdx.math.Vector2

class DelaunayVoronoiEdge(val delaunayN1:Vector2, val delaunayN2:Vector2,
                          var voronoiN1: Vector2?, var voronoiN2:Vector2?) {

    override fun hashCode(): Int {
        return delaunayN1.hashCode() + delaunayN2.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if(other!=null && other is Edge<*>){
            return (delaunayN1 == other.n1 && delaunayN2== other.n2)
                    ||
                    (delaunayN1 == other.n2 && delaunayN2 == other.n1)
        }
        return false
    }
}