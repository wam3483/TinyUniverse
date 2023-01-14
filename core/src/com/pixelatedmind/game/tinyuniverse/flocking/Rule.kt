package com.pixelatedmind.game.tinyuniverse.flocking

interface Rule {
    fun updateBoid(boid : Boid, flock : List<Boid>)
}