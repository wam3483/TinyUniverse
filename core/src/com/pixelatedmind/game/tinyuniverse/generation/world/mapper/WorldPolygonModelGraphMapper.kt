package com.pixelatedmind.game.tinyuniverse.generation.world.mapper

import com.badlogic.gdx.math.MathUtils.random
import com.pixelatedmind.game.tinyuniverse.generation.ModelFilterExecutor
import com.pixelatedmind.game.tinyuniverse.generation.world.LineInterpolator
import com.pixelatedmind.game.tinyuniverse.generation.world.model.WorldPolygonModel
import com.pixelatedmind.game.tinyuniverse.graph.*
import com.pixelatedmind.game.tinyuniverse.maps.tiled.Bitmap
import java.util.*

class WorldPolygonModelGraphMapper(val borderNoise: LineInterpolator, val landmassBitmap : Bitmap, val random : Random) {

    fun map(graph : DelaunayVoronoiGraph) : Graph<WorldPolygonModel> {

        val worldModelGraph = DVGraphToWorldModelGraphMapper().map(graph)

        val assignWaterAndLand = WaterAssignmentMutator(landmassBitmap)
        val addNoiseToEdges = WorldPolygonNoisyEdgeMutator(borderNoise)
        val assignBiomesToLand = BiomeAssignmentMutator(random,400,400)
        val findAndAssignWaterlines = WaterlineEdgeMutator()

        ModelFilterExecutor(worldModelGraph)
                .applyFilter(assignWaterAndLand)
                .applyFilter(assignBiomesToLand)
                .applyFilter(addNoiseToEdges)
                .applyFilter(findAndAssignWaterlines)

        return worldModelGraph
    }
}