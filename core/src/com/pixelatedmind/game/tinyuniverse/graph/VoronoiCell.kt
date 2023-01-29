package com.pixelatedmind.game.tinyuniverse.graph

import com.badlogic.gdx.math.Vector2

class VoronoiCell(private val indices:List<Int>, private val edges : List<TriangleVectorImpl>) {
    fun getEdges() : List<TriangleVectorImpl> {
        return indices.map{edges[it]}
    }

    fun centroid(output:Vector2=Vector2()) : Vector2{
        val centers = getEdges().map{it.centroid()}
        var centerX = 0f
        var centerY = 0f
        centers.forEach{
            centerX+=it.x
            centerY+=it.y
        }
        centerX /= centers.size
        centerY /= centers.size
        return output.set(centerX,centerY)
    }
}