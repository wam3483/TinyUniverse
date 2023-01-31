package com.pixelatedmind.game.tinyuniverse.generation.world

import com.badlogic.gdx.math.Vector2
import com.pixelatedmind.game.tinyuniverse.graph.DelaunayVoronoiEdge
import com.pixelatedmind.game.tinyuniverse.graph.DelaunayVoronoiGraph

interface LineInterpolator {
    fun interpolate(edge: DelaunayVoronoiEdge, lineComplexity:Float, lineSmoothness:Float):List<Vector2>
}