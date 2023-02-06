package com.pixelatedmind.game.tinyuniverse.renderer

import com.badlogic.gdx.graphics.Color
import com.pixelatedmind.game.tinyuniverse.extensions.color.darker
import com.pixelatedmind.game.tinyuniverse.extensions.color.lighter
import com.pixelatedmind.game.tinyuniverse.generation.world.model.Biome
import com.pixelatedmind.game.tinyuniverse.generation.world.model.WorldPolygonModel

class WorldModelRendererConfig {

    val edgeTypeColorMap = mutableMapOf<WorldPolygonModel.EdgeType, Color>()

    val biomeColorMap = mutableMapOf<Biome, Color>()

    val waterlineEdgeThickness = 2f
    init{
        initBiomeColorMap()
        initEdgeTypeColorMap()
    }

    private fun initEdgeTypeColorMap(){
        edgeTypeColorMap[WorldPolygonModel.EdgeType.Waterline] = rgb(51,51,90)
        edgeTypeColorMap[WorldPolygonModel.EdgeType.River] = rgb(34,85,136)
        edgeTypeColorMap[WorldPolygonModel.EdgeType.Lakeline] = rgb(44,53,177)
    }

    private fun initBiomeColorMap(){

        biomeColorMap[Biome.Water] = rgb(85,125,166)
        biomeColorMap[Biome.Lake] = rgb(120,128,220)

        biomeColorMap[Biome.Snow] = rgb(240,240,240)
        biomeColorMap[Biome.Tundra] = rgb(221,221,187)
        biomeColorMap[Biome.Bare] = rgb(187,187,187)
        biomeColorMap[Biome.Scorched] = rgb(153,153,153)

        biomeColorMap[Biome.Taiga] = rgb(204,212,187)
        biomeColorMap[Biome.Shrubland] = rgb(196,204,187)
        biomeColorMap[Biome.TemperateDesert] = rgb(228,232,202)

        biomeColorMap[Biome.TemperateRainForest] = rgb(156,187,169)
        biomeColorMap[Biome.TemperateDeciduousForest] = rgb(180,201,169)
        biomeColorMap[Biome.Grassland] = rgb(196,212,170)
//        biomeColorMap[Biome.TemperateDesert] = rgb(196,212,170)temperate desert is visually represented ina  chart i'm referencing twice

        biomeColorMap[Biome.TropicalRainForest] = rgb(156,187,169)

        biomeColorMap[Biome.TropicalSeasonalForest] = rgb(169,204,164)
        biomeColorMap[Biome.SubtropicalDesert] = rgb(233,221,199)
    }

    fun rgb(r:Int, g:Int, b:Int,a:Int=255) : Color{
        val r1 = r.toFloat()/255f
        val g1 = g.toFloat()/255f
        val b1 = b.toFloat()/255f
        val a1 = a.toFloat()/255f
        return Color(r1,g1,b1,a1)
    }

    fun getColorFor(edgeType : WorldPolygonModel.EdgeType) : Color {
        val color = edgeTypeColorMap[edgeType]
        if(color==null)
            return Color.RED
        return color
    }

    fun getColorFor(biome: Biome):Color{
        val color = biomeColorMap[biome]
        if(color==null)
            return Color.RED
        return color
    }
}