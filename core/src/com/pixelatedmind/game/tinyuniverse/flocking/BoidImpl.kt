package com.pixelatedmind.game.tinyuniverse.flocking

import com.badlogic.gdx.math.Vector2

class BoidImpl(val p:Vector2, val v:Vector2) : Boid {
    override fun getPosition(): Vector2 {
        return p
    }

    override fun getVelocity(): Vector2 {
        return v
    }
}