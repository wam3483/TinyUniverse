package com.pixelatedmind.game.tinyuniverse.flocking.rule

import com.badlogic.gdx.math.Vector2

interface Attractor {
    fun isAttracting() : Boolean
    fun weight() : Float
    fun getPosition() : Vector2
}