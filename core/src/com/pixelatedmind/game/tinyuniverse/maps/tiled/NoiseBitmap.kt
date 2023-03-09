package com.pixelatedmind.game.tinyuniverse.maps.tiled

import com.pixelatedmind.game.tinyuniverse.datastructure.Bitmap

class NoiseBitmap(val noiseMatrix : List<List<Double>>, val trueThreshold:Double) : Bitmap {

    override fun getWidth(): Int {
        return noiseMatrix[0].size
    }

    override fun getHeight(): Int {
        return noiseMatrix.size
    }

    override fun getValue(x: Int, y: Int): Boolean {
        return noiseMatrix[y][x]>=trueThreshold
    }
}