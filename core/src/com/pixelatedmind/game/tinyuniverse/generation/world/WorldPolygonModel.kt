package com.pixelatedmind.game.tinyuniverse.generation.world

import com.badlogic.gdx.math.Vector2

class WorldPolygonModel(val border: List<Edge>, val biome: Biome) {
    class Edge(val border:List<Vector2>, val edgeType:EdgeType, val thickness:Float){

    }
    enum class EdgeType{
        None,
        River,
        Crack
    }
}