package com.pixelatedmind.game.tinyuniverse.flocking

import com.badlogic.gdx.math.Vector2

class GenericBoidImpl<T>(val value : T) : Boid {
    private val p = Vector2()
    private val v = Vector2()
    override fun getPosition(): Vector2 {
        return p
    }

    override fun getVelocity(): Vector2 {
        return v
    }
}