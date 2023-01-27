package com.pixelatedmind.game.tinyuniverse.flocking.rule

import com.badlogic.gdx.math.Vector2
import com.pixelatedmind.game.tinyuniverse.flocking.Boid

class AttractorRuleImpl(val attractors : List<Attractor>) : Rule{
    override fun updateBoid(boid: Boid, flock: List<Boid>) {
        val activeAttractors = attractors.filter{it.isAttracting()}
        val temp = Vector2()
        val sumOfNormalizedVectors = Vector2()
        activeAttractors.forEach{
            temp.set(it.getPosition())
            temp.sub(boid.getPosition()).nor().scl(it.weight())
            sumOfNormalizedVectors.add(temp)
        }
        sumOfNormalizedVectors.nor()
        boid.getVelocity().add(sumOfNormalizedVectors).nor()
    }

}