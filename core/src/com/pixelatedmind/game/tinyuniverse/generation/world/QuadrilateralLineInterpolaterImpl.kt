package com.pixelatedmind.game.tinyuniverse.generation.world

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.pixelatedmind.game.tinyuniverse.graph.DelaunayVoronoiEdge
import com.pixelatedmind.game.tinyuniverse.graph.DelaunayVoronoiGraph
import java.util.*


class QuadrilateralLineInterpolaterImpl(val random : Random) :LineInterpolator {
    private var maxDepth = 5
    private var maxInterpolation = .25f
    private var amplitude = 1f

    //how many segments to divide each line segment into.
    //2^divisions tells you how many segments you'll get
    fun setMaxLineDivisions(divisions:Int){
        maxDepth = divisions
    }

    //tunes how much of the quadralateral formed by voronoi and delaunay vertices is available. more space
    //increases the chance the line will have greater variation
    //should be between [0, 1]
    fun setMaxInterpolation(interpolation:Float){
        this.maxInterpolation = interpolation
    }

    //tunes how much we allow noise to influence the line segments. this is just a multipler to interpolation.
    //should be between [0-1]
    fun setAmplitude(amp:Float){
        this.amplitude = amp
    }

    override fun interpolate(edge: DelaunayVoronoiEdge, lineComplexity:Float, lineSmoothness:Float): List<Vector2> {
        if(edge.voronoiN2!=null && edge.voronoiN1!=null) {
            val pointList = mutableListOf<Vector2>()

            val tempMaxDepth = (maxDepth * lineComplexity).toInt()
            val tempInterpolation = maxInterpolation * (1-lineSmoothness)

            pointList.add(edge.voronoiN1!!)
            subdivideLine(edge.delaunayN1,edge.delaunayN2,
                    edge.voronoiN1!!,edge.voronoiN2!!, random, pointList,tempMaxDepth, 1,amplitude,tempInterpolation)
            pointList.add(edge.voronoiN2!!)

            return pointList
        }
        return listOf()
    }

    private fun subdivideLine(d1:Vector2, d2:Vector2, v1:Vector2, v2:Vector2, random: Random, pointList:MutableList<Vector2>,
                              maxDepth:Int, currentDepth:Int=0, amplitude:Float=.5f, maxInterpolation:Float = .25f){
        if(currentDepth<=maxDepth){
            val noise = random.nextFloat()

            //yields a number centered about .5-maxinterpolation/2 and .5+maxinterpolation/2
            val delta = noise*maxInterpolation+(-maxInterpolation/2)
            val alpha = (.5f+(delta * amplitude))

            val midpoint = Vector2(d1).interpolate(d2, alpha, Interpolation.linear)

            val midV1D1 = Vector2(v1).interpolate(d1, .5f, Interpolation.linear)
            val midV1D2 = Vector2(v1).interpolate(d2, .5f, Interpolation.linear)
            val midV2D1 = Vector2(v2).interpolate(d1, .5f, Interpolation.linear)
            val midV2D2 = Vector2(v2).interpolate(d2, .5f, Interpolation.linear)

            subdivideLine(midV1D1, midV1D2, v1, midpoint, random, pointList, maxDepth, currentDepth+1,maxInterpolation)
            pointList.add(midpoint)
            subdivideLine(midV2D1, midV2D2, midpoint, v2,random,pointList,maxDepth, currentDepth+1,maxInterpolation)
        }
    }
}