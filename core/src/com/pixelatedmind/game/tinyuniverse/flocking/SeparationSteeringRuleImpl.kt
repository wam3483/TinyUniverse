package com.pixelatedmind.game.tinyuniverse.flocking

import com.badlogic.gdx.math.Rectangle

class SeparationSteeringRuleImpl : Rule {
    override fun updateBoid(boid: Boid, flock: List<Boid>) {
        val b1 = boid as GenericBoidImpl<Rectangle>
        val intersectingRects = flock.filter{
            val b2 = it as GenericBoidImpl<Rectangle>
            b2!=b1 && (b1.value.overlaps(b2.value) || b1.value.contains(b2.value))
        }.toList()

        intersectingRects.forEach{
            val p1 = b1.getPosition()
            val v1 = b1.getVelocity()
            val p2 = it.getPosition()
            v1.add(p2.x - p1.x, p2.y - p1.y)
            v1.scl(1F / intersectingRects.size)
            v1.scl(-1F)
            v1.nor()
        }
    }
}