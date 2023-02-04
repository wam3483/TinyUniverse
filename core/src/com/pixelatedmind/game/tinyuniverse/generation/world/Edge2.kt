package com.pixelatedmind.game.tinyuniverse.generation.world

import com.badlogic.gdx.math.Vector2

class Edge2 (val v1 :Vector2= Vector2(), val v2: Vector2 =Vector2()){

    constructor(other:Edge2) : this(Vector2(other.v1),Vector2(other.v2)) {

    }

    fun set(v1:Vector2, v2:Vector2):Edge2{
        return set(v1.x,v1.y,v2.x,v2.y)
    }

    fun set(x1:Float, y1:Float, x2:Float,y2:Float) : Edge2 {
        v1.set(x1,y1)
        v2.set(x2,y2)
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Edge2) return false

        if (v1 != other.v1 && v1 != other.v2) return false
        if (v2 != other.v1 && v2 != other.v2) return false

        return true
    }

    override fun hashCode(): Int {
        var result = v1.hashCode()
        result = 31 * result + v2.hashCode()
        return result
    }

}