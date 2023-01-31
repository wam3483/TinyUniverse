package com.pixelatedmind.game.tinyuniverse.util

import com.badlogic.gdx.math.Vector2

class Vector2Utils {
    companion object {
        fun interpolate(v1:Vector2, v2: Vector2, interpolation:Float):Vector2{
            var p1 = Vector2(v1)
            var p2 = Vector2(v2)
            p2.add(p1.sub(p2).nor().scl(interpolation))
            return p2
        }
    }
}