package com.pixelatedmind.game.tinyuniverse.extensions.bits

import com.badlogic.gdx.graphics.Color

//one's are true, zeros are false
fun Int.getBit(bitIndex : Int) : Boolean {
    return ((this shr bitIndex) and 1) == 1
}

//true is one, false is zero
fun Int.setBit(bitIndex : Int, value : Boolean) : Int {
    val mask = 1 shl bitIndex
    return if(!value){
        mask.inv() and this
    }else {
        mask or this
    }
}

fun Int.toBooleanList() : List<Boolean>{
    var i = Int.SIZE_BITS-1
    var result = mutableListOf<Boolean>()
    repeat(Int.SIZE_BITS){
        val bitValue = this.getBit(i)
        result.add(bitValue)
        --i
    }
    return result
}