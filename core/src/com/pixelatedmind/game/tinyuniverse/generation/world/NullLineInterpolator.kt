package com.pixelatedmind.game.tinyuniverse.generation.world

import com.badlogic.gdx.math.Vector2
import com.pixelatedmind.game.tinyuniverse.graph.DelaunayVoronoiEdge

class NullLineInterpolator : LineInterpolator {
    override fun interpolate(edge: DelaunayVoronoiEdge, lineComplexity: Float, lineSmoothness: Float): List<Vector2> {
        val result = mutableListOf<Vector2>()
        if(edge.voronoiN1!=null){
            result.add(edge.voronoiN1!!)
        }
        if(edge.voronoiN2!=null){
            result.add(edge.voronoiN2!!)
        }
        return result
    }
}