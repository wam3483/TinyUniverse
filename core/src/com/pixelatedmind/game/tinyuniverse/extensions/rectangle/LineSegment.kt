package com.pixelatedmind.game.tinyuniverse.extensions.rectangle

import com.badlogic.gdx.math.Vector2

class LineSegment (val v1: Vector2, val v2:Vector2){
    fun midPoint(output:Vector2 = Vector2()):Vector2{
        val smallX = Math.min(v1.x,v2.x)
        val bigX = Math.max(v1.x,v2.x)
        val smallY = Math.min(v1.y,v2.y)
        val bigY = Math.max(v1.y,v2.y)
        return output.set(
                smallX+(bigX-smallX)/2f,
                smallY+(bigY-smallY)/2f
        )
    }
}