package com.pixelatedmind.game.tinyuniverse.renderer

import com.pixelatedmind.game.tinyuniverse.generation.world.WorldModel
import space.earlygrey.shapedrawer.ShapeDrawer

class WorldModelRenderer(val shapeDrawer: ShapeDrawer, val model: WorldModel, val renderConfig: WorldModelRendererConfig) {
    private val renderModels : List<WorldPolygonRenderer>
    init {
        renderModels = model.cellGraph.getVertices().map {
            val polygonModel = WorldPolygonRenderer(it)
            polygonModel.color = renderConfig.getColorFor(it.biome)
            polygonModel
        }
    }
    fun render(){
        shapeDrawer.batch.begin()
        renderModels.forEach{
            it.render(shapeDrawer)
        }
        shapeDrawer.batch.end()
    }
}