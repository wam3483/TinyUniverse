package com.pixelatedmind.game.tinyuniverse.generation.world.model

import com.badlogic.gdx.math.Vector2
import com.pixelatedmind.game.tinyuniverse.graph.DelaunayVoronoiEdge

class WorldPolygonBiomeModel (val biome: Biome, var edges:List<DelaunayVoronoiEdge>, val centerDelaunayVertex: Vector2){

}