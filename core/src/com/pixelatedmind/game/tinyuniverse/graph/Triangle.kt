package com.pixelatedmind.game.tinyuniverse.graph

interface Triangle<T> {
    fun set(v1: T, v2:T, v3:T) : Triangle<T>
    fun centroid(out:T?) : T
}