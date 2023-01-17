package com.pixelatedmind.game.tinyuniverse.generation

import com.badlogic.gdx.math.Rectangle
import com.pixelatedmind.game.tinyuniverse.flocking.Flock
import com.pixelatedmind.game.tinyuniverse.flocking.GenericBoidImpl
import com.pixelatedmind.game.tinyuniverse.flocking.SeparationSteeringRuleImpl
import kotlin.system.measureTimeMillis

class RectangleFactorySeparationDecoratorImp(private val rectFactory:RectangleFactory, private val secsToRunSeparationSimulation : Float=20f) : RectangleFactory {
    override fun new(numRects: Int): List<Rectangle> {
        val rects = rectFactory.new(numRects)
        val boids = rects.map{ GenericBoidImpl(it) }
        val flock = createFlock(boids)

        val timestep = .01666f
        var elapsedTime = 0L
        while(anyRectanglesOverlap(rects)) {
            if(elapsedTime>=secsToRunSeparationSimulation){
                //TODO throw typed exception instead?
                throw Exception()
            }
            elapsedTime += measureTimeMillis {
                flock.update(timestep)
                //rectangle is tracked separately from position
                //but is used in a rule to determine which rects overlap, so position must be updated
                boids.forEach {
                    it.value.x = it.getPosition().x - it.value.width / 2F
                    it.value.y = it.getPosition().y - it.value.height / 2F
                }
            }
        }
        return rects
    }

    private fun createFlock(boids : List<GenericBoidImpl<Rectangle>>):Flock{
        //set boid position to center of rect it represents
        boids.forEach{it.value.getCenter(it.getPosition())}
        val rule = SeparationSteeringRuleImpl()
        val flock = Flock(boids, listOf(rule))
        return flock
    }

    private fun anyRectanglesOverlap(rects:List<Rectangle>) : Boolean{
        rects.forEach{b1->
            rects.forEach{b2->
                if(b1!=b2 && (b1.overlaps(b2) || b1.contains(b2))){
                    return false
                }
            }
        }
        return true
    }
}