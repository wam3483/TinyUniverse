package com.pixelatedmind.game.tinyuniverse.generation.world.model

enum class Biome {
    Unknown,
    Water,
    Lake,
    Snow,
    Tundra,
    Bare,
    Scorched,
    Taiga,
    Shrubland,
    TemperateDesert,
    TemperateRainForest,
    TropicalSeasonalForest,
    TropicalRainForest,
    Grassland,
    TemperateDeciduousForest,
    SubtropicalDesert
}

fun Biome.isWater() : Boolean{
    return this == Biome.Water || this == Biome.Lake
}