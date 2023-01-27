package com.pixelatedmind.game.tinyuniverse.flocking

import com.pixelatedmind.game.tinyuniverse.flocking.rule.Rule

class Flock(val boids: List<Boid>, val rules : List<Rule>) {
    val unitsPerSec = 150
    fun update(deltaSecs : Float) {
        boids.forEach{ boid ->
            rules.forEach{ rule ->
                rule.updateBoid(boid, boids)
            }
        }

        val velocity = deltaSecs * unitsPerSec
        boids.forEach{boid ->
            boid.getPosition().add(boid.getVelocity()
                    .scl(1F / rules.size)
                    .scl(velocity)
            )
            boid.getVelocity().set(0F, 0F)
        }
    }
}