package com.pixelatedmind.game.tinyuniverse.generation

interface ModelFilter<T> {
    fun applyFilter(value : T)
}