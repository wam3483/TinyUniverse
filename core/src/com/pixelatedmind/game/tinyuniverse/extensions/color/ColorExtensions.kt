package com.pixelatedmind.game.tinyuniverse.extensions.color

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Rectangle
import com.pixelatedmind.game.tinyuniverse.extensions.rectangle.LineSegment

fun Color.lighter() : Color {
    return Color(this).lerp(Color.WHITE, .5f)
}
fun Color.darker() : Color{
    return Color(this).lerp(Color.BLACK, .5f)
}