package com.pixelatedmind.game.tinyuniverse.generation

import com.badlogic.gdx.math.DelaunayTriangulator
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.pixelatedmind.game.tinyuniverse.flocking.Flock
import com.pixelatedmind.game.tinyuniverse.flocking.GenericBoidImpl
import com.pixelatedmind.game.tinyuniverse.flocking.SeparationSteeringRuleImpl
import java.util.*
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin


class WorldCellGenerator {
    private val random : Random
    val boids : MutableList<GenericBoidImpl<Rectangle>>
    private val flock : Flock
    private val triangulator : DelaunayTriangulator

    constructor(roomsToGenerate:Int, meanRoomDimension:Float, standardDeviation:Float, minRoomDim:Vector2){
        triangulator = DelaunayTriangulator()
        boids = mutableListOf<GenericBoidImpl<Rectangle>>()
        random = Random()

        generateBoids(roomsToGenerate, meanRoomDimension, standardDeviation, minRoomDim)

        //set boid position to center of rect it represents
        boids.forEach{it.value.getCenter(it.getPosition())}
        val rule = SeparationSteeringRuleImpl()
        flock = Flock(boids, listOf(rule))
    }

    private fun generateBoids(roomsToGenerate:Int, meanRoomDimension:Float, standardDeviation:Float, minRoomDim:Vector2){
        val radius = 10
        val allRects = mutableListOf<Rectangle>()
        while(allRects.size < roomsToGenerate){
            val position = getRandomPointInEllipse(radius.toFloat(), radius.toFloat())
            position.x = roundDown(position.x, 1F)
            position.y = roundDown(position.y, 1F)
            val w : Float = abs(randomNormalDist(meanRoomDimension, standardDeviation))
            val h : Float = abs(randomNormalDist(meanRoomDimension, standardDeviation))
            if(w>minRoomDim.x && h>minRoomDim.y) {
                allRects.add(
                        Rectangle(position.x, position.y,
                                roundDown(w, 1F),
                                roundDown(h, 1F))
                )
            }
        }

        boids.addAll(allRects.map{GenericBoidImpl(it)})
    }

    private fun randomNormalDist(mean : Float, std: Float) : Float{
        return mean + random.nextGaussian().toFloat() * std
    }

    fun getRooms() : List<Rectangle> {
        val avgWidth = boids.map{it.value.width}.sum()/boids.size * 1.25F
        val avgHeight = boids.map{it.value.height}.sum()/boids.size * 1.25F
        val rooms = boids.filter{
            it.value.width>=avgWidth && it.value.height >=avgHeight
        }
        return rooms.map{it.value}
    }

    private fun getRandomPointInEllipse(ellipse_width:Float, ellipse_height:Float) : Vector2{
        val t = 2*Math.PI*random.nextFloat()
        val u = random.nextFloat()+random.nextFloat()
        var r : Float? = null
        if (u > 1)
            r = 2-u
        else
            r = u
        val result = Vector2(ellipse_width*r* cos(t).toFloat()/2, ellipse_height*r* sin(t).toFloat()/2)
        return result
    }

    private fun roundDown(value:Float, units:Float) : Float {
        return value - (value % units)
    }

    //complete when there are no overlapping rectangles
    fun isGenerationComplete() : Boolean{
        boids.forEach{b1->
            boids.forEach{b2->
                if(b1!=b2 && (b1.value.overlaps(b2.value) || b1.value.contains(b2.value))){
                    return false
                }
            }
        }
        return true
    }

    fun newDelunuaryGraphFrom(nonOverlappingRects : List<Rectangle>):TriangleIndexGraph<Rectangle>{
        val graphPoints = shortArrayFrom(nonOverlappingRects)
        val triangleGraphIndices = triangulator.computeTriangles(graphPoints,false)
        val ary = ShortArray(triangleGraphIndices.size)
        var index =0
        repeat(triangleGraphIndices.size){
            ary[index] = triangleGraphIndices.get(index)
            index++
        }
        val delaunayGraph = TriangleIndexGraph(nonOverlappingRects.map{it}, ary)
        return delaunayGraph
    }

    fun shortArrayFrom(nonOverlappingRects : List<Rectangle>):FloatArray {
        val ary = FloatArray(nonOverlappingRects.size*2)
        nonOverlappingRects.forEachIndexed{index,rect->
            val i2 = index*2
            ary[i2] = rect.x
            ary[i2+1] = rect.y
        }
        return ary
    }

    fun update(deltaSecs : Float) {
        flock.update(deltaSecs)
        boids.forEach{
            it.value.x = roundDown(it.getPosition().x-it.value.width/2F,1F)
            it.value.y = roundDown(it.getPosition().y-it.value.height/2F,1F)
        }
    }
}