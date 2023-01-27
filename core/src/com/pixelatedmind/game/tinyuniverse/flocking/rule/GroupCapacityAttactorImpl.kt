package com.pixelatedmind.game.tinyuniverse.flocking.rule

import com.badlogic.gdx.math.Vector2
import com.pixelatedmind.game.tinyuniverse.flocking.Boid

class GroupCapacityAttactorImpl(val boids:List<Boid>,
                                private val p : Vector2,
                                private val weight : Float) : Attractor {

    override fun isAttracting(): Boolean {
        //TODO
        return false
    }

    override fun weight(): Float {
        return weight
    }

    override fun getPosition(): Vector2 {
        return p
    }
}