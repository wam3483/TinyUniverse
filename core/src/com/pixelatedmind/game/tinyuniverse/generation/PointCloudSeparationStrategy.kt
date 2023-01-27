package com.pixelatedmind.game.tinyuniverse.generation

import com.badlogic.gdx.math.Vector2
import com.pixelatedmind.game.tinyuniverse.flocking.BoidImpl
import com.pixelatedmind.game.tinyuniverse.flocking.Flock
import com.pixelatedmind.game.tinyuniverse.flocking.rule.SeparationSteeringRuleImpl

class PointCloudSeparationStrategy {

    fun test(points:List<Vector2>){
        val boids = points.map{ BoidImpl(it,Vector2()) }

        val separationRule = SeparationSteeringRuleImpl(10f)
        val flock = Flock(boids,listOf(separationRule))
    }
}