package com.pixelatedmind.game.tinyuniverse.maps.tiled

import com.pixelatedmind.game.tinyuniverse.datastructure.Bitmap

class IntAryBitmap : Bitmap {
    private var ary : Array<IntArray>
    private var isTrueValue : (value:Int)->Boolean

    constructor(ary : Array<IntArray>, trueValue : Int){
        this.ary = ary
        isTrueValue =  {value->value==trueValue}
    }

    constructor(ary : Array<IntArray>, isTrueValue : (value:Int)->Boolean){
        this.ary = ary
        this.isTrueValue = isTrueValue
    }

    override fun getWidth(): Int {
        return ary.size
    }

    override fun getHeight(): Int {
        return ary[0].size
    }

    override fun getValue(x: Int, y: Int): Boolean {
        if(x<0 || y<0 || y>=ary[0].size || x>=ary.size){
            return false
        }
        return isTrueValue(ary[x][y])
    }

}