package com.pixelatedmind.game.tinyuniverse.generation

class ModelFilterExecutor<T>(val value : T) {
    fun applyFilter(filter : ModelFilter<T>) : ModelFilterExecutor<T>{
        filter.applyFilter(value)
        return this
    }
}