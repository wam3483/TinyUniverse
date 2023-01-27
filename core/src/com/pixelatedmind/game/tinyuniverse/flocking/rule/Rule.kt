package com.pixelatedmind.game.tinyuniverse.flocking.rule

import com.pixelatedmind.game.tinyuniverse.flocking.Boid

interface Rule {
    fun updateBoid(boid : Boid, flock : List<Boid>)
}