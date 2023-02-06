package com.pixelatedmind.game.tinyuniverse.generation.world.mapper

import com.pixelatedmind.game.tinyuniverse.generation.ModelFilter
import com.pixelatedmind.game.tinyuniverse.generation.world.model.Biome
import com.pixelatedmind.game.tinyuniverse.generation.world.model.WorldPolygonModel
import com.pixelatedmind.game.tinyuniverse.generation.world.model.isWater
import com.pixelatedmind.game.tinyuniverse.graph.Graph

class LakeAssignmentMutator(var percentLandmassToMakeLakewater: Float, var lakeContiguousness:Float) : ModelFilter<Graph<WorldPolygonModel>> {
    override fun applyFilter(graph: Graph<WorldPolygonModel>) {
        val landVerts = graph.getVertices().filter{!it.biome.isWater()}
        val avgHeightSortedLandVerts = landVerts.map{
            val avgHeight = it.borderEdges.sumByDouble { it.height .toDouble()} / it.borderEdges.size
            Pair(avgHeight, it)
        }.sortedBy { it.first }

        val lakesToAdd = avgHeightSortedLandVerts.size * percentLandmassToMakeLakewater
        val numCommitsToCache = lakesToAdd * lakeContiguousness

        val lakeCells = mutableListOf<Pair<Double, WorldPolygonModel>>()
        var i = 0
        var lakeCellsCreated = 0

        while(lakeCellsCreated <lakesToAdd && i<avgHeightSortedLandVerts.size){
            val heightModelPair = avgHeightSortedLandVerts[i]
            val model = heightModelPair.second
            val children = graph.getChildren(model)!!
            if(children.none{it.biome.isWater()}){
                lakeCells.add(heightModelPair)
                lakeCellsCreated++
            }
            if(lakeCells.size >= numCommitsToCache){
                lakeCells.forEach{it.second.biome = Biome.Lake}
                lakeCells.clear()
            }
            i++
        }
        lakeCells.forEach{it.second.biome = Biome.Lake}
    }
}