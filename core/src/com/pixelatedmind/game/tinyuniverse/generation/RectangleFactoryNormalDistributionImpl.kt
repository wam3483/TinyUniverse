package com.pixelatedmind.game.tinyuniverse.generation

import com.badlogic.gdx.math.Rectangle
import java.util.*
import kotlin.math.abs

class RectangleFactoryNormalDistributionImpl(private val positionFactory:VectorFactory,
                                             private val random: Random,
                                             private val averageRectDim:Float,
                                             private val standardRectDimDeviation : Float,
                                             private val minRoomWidth:Float,
                                             private val minRoomHeight:Float): RectangleFactory{

    override fun new(numRects: Int): List<Rectangle> {
        val allRects = mutableListOf<Rectangle>()
        while(allRects.size < numRects){
            val position = positionFactory.new()
            val w : Float = abs(randomNormalDist())
            val h : Float = abs(randomNormalDist())
            position.sub(w/2,h/2)
            if(w>minRoomWidth && h>minRoomHeight) {
                allRects.add(Rectangle(position.x, position.y, w, h))
            }
        }
        return allRects
    }

    private fun randomNormalDist() : Float{
        return averageRectDim + random.nextGaussian().toFloat() * standardRectDimDeviation
    }
}