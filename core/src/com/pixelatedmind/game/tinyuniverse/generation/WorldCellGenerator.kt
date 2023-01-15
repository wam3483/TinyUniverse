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

    constructor(roomsToGenerate:Int, meanRoomDimension:Float, standardDeviation:Float, minRoomDim:Vector2, seed:Long?=null){
        triangulator = DelaunayTriangulator()
        boids = mutableListOf<GenericBoidImpl<Rectangle>>()
        val seedInput = seed ?: Random().nextLong()
        random = Random(seedInput)

        generateBoids(roomsToGenerate, meanRoomDimension, standardDeviation, minRoomDim)

        //set boid position to center of rect it represents
        boids.forEach{it.value.getCenter(it.getPosition())}
        val rule = SeparationSteeringRuleImpl()
        flock = Flock(boids, listOf(rule))
        println("Dungeon seed:"+seedInput)
    }

    private fun generateBoids(roomsToGenerate:Int, meanRoomDimension:Float, standardDeviation:Float, minRoomDim:Vector2){
        val radius = 10
        val allRects = mutableListOf<Rectangle>()
        while(allRects.size < roomsToGenerate){
            val position = getRandomPointInEllipse(radius.toFloat(), radius.toFloat())
            val w : Float = abs(randomNormalDist(meanRoomDimension, standardDeviation))
            val h : Float = abs(randomNormalDist(meanRoomDimension, standardDeviation))
            position.sub(w/2,h/2)
            position.x = roundDown(position.x, 1F)
            position.y = roundDown(position.y, 1F)
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
            it.value.width>=avgWidth || it.value.height >=avgHeight
        }
        return rooms.map{it.value}
    }

    private fun getRandomPointInEllipse(ellipse_width:Float, ellipse_height:Float) : Vector2{
        val t = 2*Math.PI*random.nextFloat()
        val u = random.nextFloat()+random.nextFloat()
        var r : Float?
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

    fun newDelunuaryGraphFrom(roomRects : List<Rectangle>):TriangleIndexGraph<Rectangle>{
        val graphPoints = shortArrayFrom(roomRects)
        val triangleGraphIndices = triangulator.computeTriangles(graphPoints,false)
        val ary = ShortArray(triangleGraphIndices.size)
        var index =0
        repeat(triangleGraphIndices.size){
            ary[index] = triangleGraphIndices.get(index)
            index++
        }
        val delaunayGraph = TriangleIndexGraph(roomRects.map{it}, ary)
        return delaunayGraph
    }

    fun newMainRoomGraph(roomRects : List<Rectangle>):List<Edge<Rectangle>>{
        val connectedGraph = newDelunuaryGraphFrom(roomRects)
        val spanningTree = connectedGraph.getSpanningTree()
        val edges = spanningTree.getEdges().toMutableList()
        val numEdgesToAdd = (edges.size * .15f).toInt()
        var numAdded = 0
        while(numAdded<numEdgesToAdd){
            val edge = edges[random.nextInt(edges.size)]
            val n1children = connectedGraph.getChildren(edge.n1.value)!!
            val e2 = Edge(edge.n1, n1children[random.nextInt(n1children.size)])
            if (edges.none { it.equals(e2) }) {
                numAdded++
                edges.add(e2)
            }
        }
        val hallways = findHallways(edges,5f)
        return edges
    }
    fun Rectangle.right() : Float{
        return x+width
    }
    fun Rectangle.bottom() : Float{
        return y+height
    }
    fun Rectangle.xAxisOverlap(other:Rectangle, output: Vector2) : Vector2?{
        if(x>other.right() || right()< other.x){
            return null
        }
        return output.set(
            x.coerceAtLeast(other.x),
            right().coerceAtMost(other.right())
        )
    }
    fun Rectangle.yAxisOverlap(other:Rectangle, output:Vector2):Vector2?{
        if(y>other.bottom() || y<other.y){
            return null
        }
        return output.set(
            y.coerceAtLeast(other.y),
            bottom().coerceAtMost(other.bottom())
        )
    }

    private fun findHallways(edges:List<Edge<Rectangle>>, hallwaySize:Float):List<Rectangle>{
        val v1 = Vector2()
        val v2 = Vector2()
        val output = mutableListOf<Rectangle>()
        edges.forEach {
            val xOverlap = it.n1.value.xAxisOverlap(it.n2.value, v1)
            val yOverlap = it.n1.value.yAxisOverlap(it.n2.value, v2)
            //both should never be non-null at the same time
            //
            //if both are null OR
            //overlapped on X axis but overlap was too small OR
            //overlapped on y axis but overlap was too small
            //  then we do L shape
            if((xOverlap==null && yOverlap==null) ||
                (xOverlap!=null && (xOverlap.y-xOverlap.x)>hallwaySize) ||
                (yOverlap!=null && (yOverlap.y-yOverlap.x)>hallwaySize)){
                //L shape

            }
            else if(xOverlap!=null && xOverlap.y-xOverlap.x>hallwaySize){
                val left = xOverlap.x+(xOverlap.y-xOverlap.x)/2-hallwaySize/2
                val rect = Rectangle()
                rect.x = left
                rect.width = hallwaySize
                if(it.n1.value.y<it.n2.value.y){
                    rect.y = it.n1.value.bottom()
                    rect.height = it.n2.value.y-it.n1.value.bottom()
                }
                else{
                    rect.y = it.n2.value.bottom()
                    rect.height = it.n1.value.y - it.n2.value.bottom()
                }
                output.add(rect)
            }
            else if(yOverlap!=null && yOverlap.y-yOverlap.y>hallwaySize){

            }
        }
        return output
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

    var timeAccumulator = 0f
    val timestep = .01666f
    fun update(deltaSecs : Float) {
        timeAccumulator+=deltaSecs
        while(timeAccumulator>=timestep) {
            timeAccumulator -= timestep
            flock.update(timestep)
            boids.forEach {
                it.value.x = roundDown(it.getPosition().x - it.value.width / 2F, 1F)
                it.value.y = roundDown(it.getPosition().y - it.value.height / 2F, 1F)
            }
        }
    }
}