package com.pixelatedmind.game.tinyuniverse.maps.tiled

interface Bitmap {
    fun getWidth():Int
    fun getHeight():Int
    fun getValue(x:Int,y:Int):Boolean
}