package com.pixelatedmind.game.tinyuniverse.flocking.rule

import com.badlogic.gdx.math.Vector2
import com.pixelatedmind.game.tinyuniverse.flocking.Boid

class SeparationSteeringRuleImpl(val separationRadius:Float) : Rule {
    override fun updateBoid(boid: Boid, flock: List<Boid>) {
        val neighbors = flock.filter {
            it.getPosition().dst(boid.getPosition()) <= separationRadius
        }.toList()

        val temp = Vector2()
        val sumOfNormalizedVectors = Vector2()
        neighbors.forEach {
            val v1 = boid.getVelocity()

            val p1 = boid.getPosition()
            val p2 = it.getPosition()
            //point velocity in opposite direction of neighbor
            //normalize vector so all are weighted equally
            temp.set(p2).sub(p1).scl(-1f).nor()
            sumOfNormalizedVectors.add(temp)
        }
        sumOfNormalizedVectors.nor()
        boid.getVelocity().add(sumOfNormalizedVectors).nor()
    }
}