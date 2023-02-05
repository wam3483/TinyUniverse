package com.pixelatedmind.game.tinyuniverse.generation.world

import com.badlogic.gdx.math.Vector2
import com.pixelatedmind.game.tinyuniverse.graph.DelaunayVoronoiEdge

interface LineInterpolator {
    fun interpolate(edge: DelaunayVoronoiEdge, lineResolution:Float, lineSmoothness:Float):List<Vector2>
}