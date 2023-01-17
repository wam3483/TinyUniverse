package com.pixelatedmind.game.tinyuniverse.extensions.rectangle

class Range(val min:Float, val max:Float) {
    fun inRange(input:Float):Boolean{
        return input in min..max
    }
    fun size():Float{
        return max - min
    }
    fun mid():Float{
        return min+size()/2
    }
}