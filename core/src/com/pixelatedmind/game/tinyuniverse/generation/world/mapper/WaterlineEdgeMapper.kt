package com.pixelatedmind.game.tinyuniverse.generation.world.mapper

import com.pixelatedmind.game.tinyuniverse.generation.world.model.Biome
import com.pixelatedmind.game.tinyuniverse.generation.world.model.WorldPolygonModel
import com.pixelatedmind.game.tinyuniverse.graph.Graph

class WaterlineEdgeMapper {
    fun map(graph : Graph<WorldPolygonModel>){
        graph.getVertices().filter{it.biome==Biome.Water}.forEach{worldModel->
            worldModel.borderEdges.forEach{edge->
                val otherModel = edge.adjacentWorldPolygonModels.firstOrNull{it!=worldModel}
                if(otherModel != null && otherModel.biome != Biome.Water){
                    edge.edgeType = WorldPolygonModel.EdgeType.Waterline
                }
            }
        }
    }
}