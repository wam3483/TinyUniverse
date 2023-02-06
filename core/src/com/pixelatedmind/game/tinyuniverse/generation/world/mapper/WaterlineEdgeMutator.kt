package com.pixelatedmind.game.tinyuniverse.generation.world.mapper

import com.pixelatedmind.game.tinyuniverse.generation.ModelFilter
import com.pixelatedmind.game.tinyuniverse.generation.world.model.Biome
import com.pixelatedmind.game.tinyuniverse.generation.world.model.WorldPolygonModel
import com.pixelatedmind.game.tinyuniverse.graph.Graph

class WaterlineEdgeMutator : ModelFilter<Graph<WorldPolygonModel>> {
    fun map(graph : Graph<WorldPolygonModel>){
        graph.getVertices().filter{it.biome==Biome.Water}.forEach{waterModel->
            waterModel.borderEdges.forEach{edge->
                val otherModel = edge.adjacentWorldPolygonModels.firstOrNull{it!=waterModel}
                if(otherModel != null && otherModel.biome != Biome.Water){
                    edge.edgeType = WorldPolygonModel.EdgeType.Waterline
                }
            }
        }
        graph.getVertices().filter{it.biome==Biome.Lake}.forEach{lakeModel->
            lakeModel.borderEdges.forEach{edge->
                val otherModel = edge.adjacentWorldPolygonModels.firstOrNull{it!=lakeModel}
                if(otherModel != null && otherModel.biome != Biome.Lake){
                    edge.edgeType = WorldPolygonModel.EdgeType.Lakeline
                }
            }
        }
    }

    override fun applyFilter(value: Graph<WorldPolygonModel>) {
        map(value)
    }
}