package com.pixelatedmind.game.tinyuniverse.generation

import com.badlogic.gdx.math.Vector2
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class VectorFactoryEllipseImpl(private val width:Float, private val height:Float, private val random: Random) :VectorFactory{
    override fun new(): Vector2 {
        val t = 2*Math.PI*random.nextFloat()
        val u = random.nextFloat()+random.nextFloat()
        var r : Float?
        if (u > 1)
            r = 2-u
        else
            r = u
        val result = Vector2(width*r* cos(t).toFloat()/2, height*r* sin(t).toFloat()/2)
        return result
    }
}