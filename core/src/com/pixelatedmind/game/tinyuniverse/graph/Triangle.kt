package com.pixelatedmind.game.tinyuniverse.graph

import com.badlogic.gdx.math.GeometryUtils
import com.badlogic.gdx.math.Vector2

class Triangle(val v1: Vector2, val v2:Vector2, val v3:Vector2) {
    fun centroid(output : Vector2 = Vector2()) : Vector2{
        return GeometryUtils.triangleCentroid(v1.x,v1.y,v2.x,v2.y,v3.x,v3.y,output)
    }

    fun hasSharedEdge(t:Triangle) : Boolean{
        if(checkEdge(t.v1,t.v2) || checkEdge(t.v2,t.v3) || checkEdge(t.v3,t.v1)){
            return true
        }
        return false
    }

    private fun checkEdge(otherV1:Vector2, otherV2:Vector2) : Boolean{
        if((v1==otherV1 && v2==otherV2) || (v2==otherV1 && v1==otherV2)){
            return true
        }
        if((v2==otherV1 && v3==otherV2) || (v3==otherV1 && v2==otherV2)){
            return true
        }
        if((v3==otherV1 && v1==otherV2) || (v1==otherV1 && v3==otherV2)){
            return true
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if(other is Triangle) {
            return other.v1 == v1 && other.v2 == v2 && other.v3 == v3
        }
        return false
    }

    override fun hashCode(): Int {
        return v1.hashCode()+v2.hashCode()+v3.hashCode()
    }
}