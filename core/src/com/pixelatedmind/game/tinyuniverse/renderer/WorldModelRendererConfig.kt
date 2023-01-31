package com.pixelatedmind.game.tinyuniverse.renderer

import com.badlogic.gdx.graphics.Color
import com.pixelatedmind.game.tinyuniverse.extensions.color.darker
import com.pixelatedmind.game.tinyuniverse.extensions.color.lighter
import com.pixelatedmind.game.tinyuniverse.generation.world.Biome

class WorldModelRendererConfig {

    val biomeColorMap = mutableMapOf<Biome, Color>()
    init{
        biomeColorMap[Biome.Unknown] = Color.RED
        biomeColorMap[Biome.Water] = Color(.2f,.4f,.6f,1f)
        biomeColorMap[Biome.Grassland] = Color(.53f, .66f,.33f, 1f)
        biomeColorMap[Biome.Forest] = Color(.39f,.58f,.35f,1f)//Color.GREEN
        biomeColorMap[Biome.Jungle] = Color(.2f,.47f,.33f,1f)//Color.GREEN.darker()
        biomeColorMap[Biome.Mountains] = Color.LIGHT_GRAY
        biomeColorMap[Biome.Desert] = Color.BROWN.lighter()
        biomeColorMap[Biome.Tundra] = Color.WHITE
    }

    fun getColorFor(biome:Biome):Color{
        return biomeColorMap[biome]!!
    }
}