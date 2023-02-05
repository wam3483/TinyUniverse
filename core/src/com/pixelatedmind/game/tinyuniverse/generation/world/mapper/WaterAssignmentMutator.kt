package com.pixelatedmind.game.tinyuniverse.generation.world.mapper

import com.pixelatedmind.game.tinyuniverse.generation.ModelFilter
import com.pixelatedmind.game.tinyuniverse.generation.world.model.Biome
import com.pixelatedmind.game.tinyuniverse.generation.world.model.WorldPolygonModel
import com.pixelatedmind.game.tinyuniverse.graph.Graph
import com.pixelatedmind.game.tinyuniverse.maps.tiled.Bitmap

/***
 * Assigns voronoi regions as either water biome, or unknown by querying a bitmap for true/false values respectively.
 * Land bitmap should return true to indicate region should be land, and false for water.
 */
class WaterAssignmentMutator(val landBitmap: Bitmap) : ModelFilter<Graph<WorldPolygonModel>> {

    private fun map(graph : Graph<WorldPolygonModel>) {
        graph.getVertices().forEach {
            val delaunayVertex = it.delaunayVertex
            val isLandBiome = landBitmap.getValue(delaunayVertex.x.toInt(), delaunayVertex.y.toInt())
            val biome = when (isLandBiome) {
                true -> Biome.Water
                else -> Biome.Unknown
            }
            it.biome = biome
        }
    }

    override fun applyFilter(value: Graph<WorldPolygonModel>){
        map(value)
    }
}