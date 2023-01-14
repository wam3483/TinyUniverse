package com.pixelatedmind.game.tinyuniverse.flocking

import com.badlogic.gdx.math.Vector2

interface Boid {
    fun getPosition() : Vector2
    fun getVelocity() : Vector2
}