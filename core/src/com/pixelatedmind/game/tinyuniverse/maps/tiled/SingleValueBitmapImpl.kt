package com.pixelatedmind.game.tinyuniverse.maps.tiled

import com.pixelatedmind.game.tinyuniverse.datastructure.Bitmap

class SingleValueBitmapImpl(val value : Boolean, val w:Int=1,val h:Int=1) : Bitmap {

    override fun getWidth(): Int {
        return w
    }

    override fun getHeight(): Int {
        return h
    }

    override fun getValue(x: Int, y: Int): Boolean {
        return value
    }
}