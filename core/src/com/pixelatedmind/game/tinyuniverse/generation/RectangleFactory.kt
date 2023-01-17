package com.pixelatedmind.game.tinyuniverse.generation

import com.badlogic.gdx.math.Rectangle

interface RectangleFactory {
    fun new(numRects:Int) : List<Rectangle>
}