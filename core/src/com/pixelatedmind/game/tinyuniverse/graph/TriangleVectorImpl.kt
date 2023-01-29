package com.pixelatedmind.game.tinyuniverse.graph

import com.badlogic.gdx.math.GeometryUtils
import com.badlogic.gdx.math.Vector2

class TriangleVectorImpl(var v1: Vector2=Vector2(), var v2:Vector2=Vector2(), var v3:Vector2=Vector2()) : Triangle<Vector2>{
    override fun centroid(output : Vector2?) : Vector2 {
        var temp = output
        if(temp== null){
            temp = Vector2()
        }
        return GeometryUtils.triangleCentroid(v1.x,v1.y,v2.x,v2.y,v3.x,v3.y,temp)
    }

    override fun set(t1:Vector2, t2:Vector2, t3:Vector2):Vector2{
        this.v1 = t1
        this.v2 = t2
        this.v3 = t3
        return this
    }

    fun hasSharedEdge(t:TriangleVectorImpl) : Boolean{
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
        if(other is TriangleVectorImpl) {
            return other.v1 == v1 && other.v2 == v2 && other.v3 == v3
        }
        return false
    }

    override fun hashCode(): Int {
        return v1.hashCode()+v2.hashCode()+v3.hashCode()
    }
}