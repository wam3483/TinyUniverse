package com.pixelatedmind.game.tinyuniverse.generation.world.model

import com.pixelatedmind.game.tinyuniverse.generation.world.Biome
import com.pixelatedmind.game.tinyuniverse.graph.DelaunayVoronoiEdge

class WorldPolygonBiomeModel (val biome: Biome, var edges:List<DelaunayVoronoiEdge>){
}