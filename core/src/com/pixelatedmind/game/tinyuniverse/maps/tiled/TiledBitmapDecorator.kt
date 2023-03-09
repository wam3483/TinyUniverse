package com.pixelatedmind.game.tinyuniverse.maps.tiled

import com.pixelatedmind.game.tinyuniverse.datastructure.Bitmap

class TiledBitmapDecorator(val bitmap : Bitmap) : Bitmap {
    override fun getWidth(): Int {
        return bitmap.getWidth()
    }

    override fun getHeight(): Int {
        return bitmap.getHeight()
    }

    override fun getValue(x: Int, y: Int): Boolean {
        val x1 = x % bitmap.getWidth()
        val y1 = y % bitmap.getHeight()
        return bitmap.getValue(x1,y1)
    }
}